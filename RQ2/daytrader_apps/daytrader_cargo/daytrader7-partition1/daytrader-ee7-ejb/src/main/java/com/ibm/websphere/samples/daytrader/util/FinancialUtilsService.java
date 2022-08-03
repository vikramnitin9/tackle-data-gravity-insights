package com.ibm.websphere.samples.daytrader.util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import com.ibm.cardinal.util.CardinalException;
import com.ibm.cardinal.util.CardinalLogger;
import com.ibm.cardinal.util.CardinalString;
import com.ibm.cardinal.util.ClusterObjectManager;
import com.ibm.cardinal.util.SerializationUtil;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import com.ibm.websphere.samples.daytrader.entities.HoldingDataBean;

/**
 * Service class for FinancialUtils - Generated by Cardinal
 */

@Path("/FinancialUtilsService")
public class FinancialUtilsService {
    private static final Logger logger = CardinalLogger.getLogger(FinancialUtilsService.class);

    // default constructor service
    @POST
    @Path("/FinancialUtils_default_ctor")
    @Produces(MediaType.APPLICATION_JSON) 
    public Response FinancialUtils_default_ctor() {
        FinancialUtils instFinancialUtils = new FinancialUtils();
        String refid = ClusterObjectManager.putObject(instFinancialUtils);
        instFinancialUtils.setKlu__referenceID(refid);
        JsonObject jsonobj = Json
            .createObjectBuilder()
            .add("return_value", refid)
            .build();
        logger.info("[FinancialUtilsService] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();
    }



    // health check service
    @GET 
    @Path("/health") 
    @Produces(MediaType.TEXT_HTML) 
    public String getHealth() { 
        logger.info("[FinancialUtils] getHealth() called");
        return "FinancialUtilsService::Health OK"; 
    }



    // service for incrementing object reference count
    @POST
    @Path("/incObjectCount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void incObjectCount(@FormParam("klu__referenceID") String klu__referenceID) {
        logger.info("[FinancialUtilsService] incObjectCount() called with ref: "+klu__referenceID);
        ClusterObjectManager.incObjectCount(klu__referenceID);
    }



    // service for decrementing object reference count
    @POST
    @Path("/decObjectCount")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void decObjectCount(@FormParam("klu__referenceID") String klu__referenceID) {
        logger.info("[FinancialUtils] decObjectCount() called with ref: "+klu__referenceID);
        ClusterObjectManager.decObjectCount(klu__referenceID);
    }





    @POST
    @Path("/computeGain")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response computeGain(
        @FormParam("currentBalance") String currentBalance,
        @FormParam("openBalance") String openBalance,
        @Context HttpServletResponse servletResponse
    ) {

        
        // convert reference ID(s) stored in "currentBalance" to physical/proxy object(s)
        BigDecimal currentBalance_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(currentBalance);

        
        // convert reference ID(s) stored in "openBalance" to physical/proxy object(s)
        BigDecimal openBalance_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(openBalance);

        BigDecimal response;

        try {
        // call static method
            response = FinancialUtils.computeGain(currentBalance_fpar, openBalance_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method computeGain() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        
        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();

        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/computeGainPercent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response computeGainPercent(
        @FormParam("currentBalance") String currentBalance,
        @FormParam("openBalance") String openBalance,
        @Context HttpServletResponse servletResponse
    ) {

        
        // convert reference ID(s) stored in "currentBalance" to physical/proxy object(s)
        BigDecimal currentBalance_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(currentBalance);

        
        // convert reference ID(s) stored in "openBalance" to physical/proxy object(s)
        BigDecimal openBalance_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(openBalance);

        BigDecimal response;

        try {
        // call static method
            response = FinancialUtils.computeGainPercent(currentBalance_fpar, openBalance_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method computeGainPercent() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        
        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();

        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/computeHoldingsTotal")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response computeHoldingsTotal(
        @FormParam("holdingDataBeans") String holdingDataBeans,
        @Context HttpServletResponse servletResponse
    ) {

        
        // convert reference ID(s) stored in "holdingDataBeans" to physical/proxy object(s)
        Collection<?> holdingDataBeans_fpar = (Collection<?>)SerializationUtil.decodeWithDynamicTypeCheck(holdingDataBeans);

        BigDecimal response;

        try {
        // call static method
            response = FinancialUtils.computeHoldingsTotal(holdingDataBeans_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method computeHoldingsTotal() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        
        // convert physical/proxy object(s) referenced by "response" to reference ID(s)
        String response_obj = SerializationUtil.encodeWithDynamicTypeCheck(response);
        JsonObject jsonobj = jsonresp.add("return_value", response_obj).build();

        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/printGainHTML")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response printGainHTML(
        @FormParam("gain") String gain,
        @Context HttpServletResponse servletResponse
    ) {

        
        // convert reference ID(s) stored in "gain" to physical/proxy object(s)
        BigDecimal gain_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(gain);

        String response;

        try {
        // call static method
            response = FinancialUtils.printGainHTML(gain_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method printGainHTML() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        JsonObject jsonobj = jsonresp.add("return_value", response).build();
        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/printChangeHTML")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response printChangeHTML(
        @FormParam("change") String change,
        @Context HttpServletResponse servletResponse
    ) {

        double change_fpar = Double.parseDouble(change);

        String response;

        try {
        // call static method
            response = FinancialUtils.printChangeHTML(change_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method printChangeHTML() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        JsonObject jsonobj = jsonresp.add("return_value", response).build();
        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/printGainPercentHTML")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response printGainPercentHTML(
        @FormParam("gain") String gain,
        @Context HttpServletResponse servletResponse
    ) {

        
        // convert reference ID(s) stored in "gain" to physical/proxy object(s)
        BigDecimal gain_fpar = (BigDecimal)SerializationUtil.decodeWithDynamicTypeCheck(gain);

        String response;

        try {
        // call static method
            response = FinancialUtils.printGainPercentHTML(gain_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method printGainPercentHTML() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        JsonObject jsonobj = jsonresp.add("return_value", response).build();
        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

    @POST
    @Path("/printQuoteLink")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response printQuoteLink(
        @FormParam("symbol") String symbol,
        @Context HttpServletResponse servletResponse
    ) {

        String symbol_fpar = symbol;

        String response;

        try {
        // call static method
            response = FinancialUtils.printQuoteLink(symbol_fpar);
        }
        catch (Throwable t) {
            String msg = "Call to static method printQuoteLink() of FinancialUtils raised exception"+t.getMessage();
            logger.warning(msg);
            throw new WebApplicationException(msg, t, CardinalException.APPLICATION_EXCEPTION);
        }
        JsonObjectBuilder jsonresp = Json.createObjectBuilder();
        JsonObject jsonobj = jsonresp.add("return_value", response).build();
        logger.info("[FinancialUtils] Returning JSON object: "+jsonobj.toString());
        return Response
            .status(Response.Status.OK)
            .entity(jsonobj)
            .build();

    }

}