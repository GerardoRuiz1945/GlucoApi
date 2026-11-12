/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.lasalle.controller.MedidaController;
import org.lasalle.controller.UsuarioController;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;

/**
 *
 * @author ruizt
 */
@Path("glucosa")
public class RestGlucosa {

    private final Gson gson = new Gson();

    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar() {
        String saludo = """
                        {"Saludo":"Hola dsde java"}
                        """;
        return Response.ok(saludo).build();
    }

    @Path("SaludarCumpleanios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarCumpleanios(
            @QueryParam("nombre") String nombre) {
        String out = """
                     {"response" : "Feliz cumple %s"}
                     """;
        out = String.format(out, nombre);
        return Response.ok(out).build();
    }

    @Path("usuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuarios() {
        UsuarioController uc = new UsuarioController();
        List<Usuario> lista;
        try {
            lista = uc.getUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "No se pudieron obtener los usuarios: " + e.getMessage());
        }
        return Response.ok(gson.toJson(lista), MediaType.APPLICATION_JSON).build();
    }

    /**
     * POST /api/glucosa/registraUsuario
     *
     * application/x-www-form-urlencoded:
     * nombre, primerApellido, segundoApellido (opcional), email
     */
    @Path("registraUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email) {

        if (isBlank(nombre) || isBlank(primerApellido) || isBlank(email)) {
            return error(Response.Status.BAD_REQUEST,
                    "Los campos nombre, primerApellido y email son obligatorios");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setPrimerApellido(primerApellido.trim());
        usuario.setSegundoApellido(isBlank(segundoApellido) ? null : segundoApellido.trim());
        usuario.setEmail(email.trim());

        try {
            UsuarioController uc = new UsuarioController();
            int idUsuario = uc.registrarUsuario(usuario);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("result", "ok");
            body.put("message", "Usuario registrado correctamente");
            body.put("idUsuario", idUsuario);
            body.put("usuario", usuario);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(body))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "No se pudo registrar el usuario: " + e.getMessage());
        }
    }

    /**
     * POST /api/glucosa/registrarMedida
     *
     * application/x-www-form-urlencoded:
     * idUsuario, glucosa, fechaHora (opcional, formato yyyy-MM-dd HH:mm:ss),
     * contexto (opcional), notas (opcional)
     */
    @Path("registrarMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarMedida(
            @FormParam("idUsuario") String idUsuario,
            @FormParam("glucosa") String glucosa,
            @FormParam("fechaHora") String fechaHora,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas) {

        if (isBlank(idUsuario) || isBlank(glucosa)) {
            return error(Response.Status.BAD_REQUEST,
                    "Los campos idUsuario y glucosa son obligatorios");
        }

        int idUsuarioInt;
        int glucosaInt;
        try {
            idUsuarioInt = Integer.parseInt(idUsuario.trim());
            glucosaInt = Integer.parseInt(glucosa.trim());
        } catch (NumberFormatException e) {
            return error(Response.Status.BAD_REQUEST,
                    "idUsuario y glucosa deben ser numeros enteros");
        }

        if (idUsuarioInt <= 0 || glucosaInt <= 0) {
            return error(Response.Status.BAD_REQUEST,
                    "idUsuario y glucosa deben ser mayores a cero");
        }

        Medida medida = new Medida();
        medida.setIdUsuario(idUsuarioInt);
        medida.setGlucosa(glucosaInt);
        medida.setFechaHora(normalizarFecha(fechaHora));
        medida.setContexto(isBlank(contexto) ? null : contexto.trim());
        medida.setNotas(isBlank(notas) ? null : notas.trim());

        try {
            MedidaController mc = new MedidaController();
            int idMedida = mc.registrarMedida(medida);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("result", "ok");
            body.put("message", "Medida registrada correctamente");
            body.put("idMedida", idMedida);
            body.put("medida", medida);
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(body))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (SQLIntegrityConstraintViolationException e) {
            return error(Response.Status.BAD_REQUEST,
                    "El idUsuario " + idUsuarioInt + " no existe");
        } catch (Exception e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "No se pudo registrar la medida: " + e.getMessage());
        }
    }

    /**
     * GET /api/glucosa/medidas?idUsuario=1
     */
    @Path("medidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidas(@QueryParam("idUsuario") int idUsuario) {
        try {
            MedidaController mc = new MedidaController();
            List<Medida> lista = mc.getMedidasPorUsuario(idUsuario);
            return Response.ok(gson.toJson(lista), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace();
            return error(Response.Status.INTERNAL_SERVER_ERROR,
                    "No se pudieron obtener las medidas: " + e.getMessage());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Acepta "yyyy-MM-dd HH:mm:ss" o "yyyy-MM-ddTHH:mm[:ss]" y lo deja
     * en el formato que espera Timestamp.valueOf(...).
     */
    private static String normalizarFecha(String fechaHora) {
        if (isBlank(fechaHora)) {
            return null;
        }
        String fecha = fechaHora.trim().replace('T', ' ');
        if (fecha.length() == 16) {
            fecha = fecha + ":00";
        }
        return fecha;
    }

    private static Response error(Response.Status status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("result", "error");
        body.put("message", message);
        return Response.status(status)
                .entity(new Gson().toJson(body))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
