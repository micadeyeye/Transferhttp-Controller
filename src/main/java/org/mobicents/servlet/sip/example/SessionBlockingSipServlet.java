/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletContextEvent;
import javax.servlet.sip.SipServletListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

/**
 * This examle shows Session Blocking Capabilities. It makes the sip servlet acts as
 * a UAS.
 * Based on the from header it blocks or forwards the web session if it finds the address in its hard coded black list.
 * 
 * @author Jean Deruelle; modified by Michael Adeyeye
 * 
 */
public class SessionBlockingSipServlet extends SipServlet implements SipServletListener {

    private static Logger logger = Logger.getLogger(SessionBlockingSipServlet.class);
    List<String> blockedUris = null;
    Map<String, String[]> forwardingUris = null;

    /** Creates a new instance of SessionBlockingSipServlet */
    public SessionBlockingSipServlet() {
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("the web session blocking sip servlet has been started");
        super.init(servletConfig);
        blockedUris = new ArrayList<String>();
        blockedUris.add("sip:blocked-sender@testdomain.com");
        blockedUris.add("sip:blocked-sender@127.0.0.1");
        forwardingUris = new HashMap<String, String[]>();
        forwardingUris.put("sip:receiver@testdomain.com",
                new String[]{"sip:forward-receiver@testdomain.com", "sip:forward-receiver@127.0.0.1:5091"});
        forwardingUris.put("sip:receiver@127.0.0.1",
                new String[]{"sip:forward-receiver@testdomain.com", "sip:forward-receiver@127.0.0.1:5091"});
    }

    @Override
    protected void doRegister(SipServletRequest req) throws ServletException, IOException {
        logger.info("Received register request: " + req.getTo());
        int response = SipServletResponse.SC_OK;
        SipServletResponse resp = req.createResponse(response);
        resp.send();
    }

    @Override
    protected void doInvite(SipServletRequest request) throws ServletException,
            IOException {

        logger.info("Got request:\n" + request.toString());
        String fromUri = request.getFrom().getURI().toString();
        String to = request.getTo().getURI().toString();
        logger.info(fromUri);
        String[] forwardingUri = forwardingUris.get(request.getTo().getURI().toString());
        B2buaHelper helper = request.getB2buaHelper();

        if (blockedUris.contains(fromUri)) {
            logger.info(fromUri + " has been blocked !");
            SipServletResponse sipServletResponse = request.createResponse(SipServletResponse.SC_FORBIDDEN);
            sipServletResponse.send();
            logger.info("Keeping record of the session transfer request");
            //store the session transfer request
            sessiontracking sessiontransfer = new sessiontracking();
            sessiontransfer.storesessioninfo(fromUri, to, "INVITE", "Forbidden", "");
        } else {
            if (forwardingUri != null && forwardingUri.length > 0) {

                SipFactory sipFactory = (SipFactory) getServletContext().getAttribute(
                        SIP_FACTORY);

                Map<String, List<String>> headers = new HashMap<String, List<String>>();
                List<String> toHeaderSet = new ArrayList<String>();
                toHeaderSet.add(forwardingUri[0]);
                headers.put("To", toHeaderSet);

                SipServletRequest forkedRequest = helper.createRequest(request, true,
                        headers);
                SipURI sipUri = (SipURI) sipFactory.createURI(forwardingUri[1]);
                forkedRequest.setRequestURI(sipUri);
                if (logger.isInfoEnabled()) {
                    logger.info("forkedRequest = " + forkedRequest);
                }
                forkedRequest.getSession().setAttribute("originalRequest", request);

                forkedRequest.send();
                logger.info("Keeping record of the session transfer request");
                //store the session transfer request
                sessiontracking sessiontransfer = new sessiontracking();
                sessiontransfer.storesessioninfo(fromUri, to, "INVITE", "Forwarded", "");
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Call has been proxied.");
                    //store the session transfer request
                    sessiontracking sessiontransfer = new sessiontracking();
                    sessiontransfer.storesessioninfo(fromUri, to, "INVITE", "Proxied", "");
                }
            }
        }
    }

    @Override
    protected void doMessage(SipServletRequest req) throws ServletException, IOException {
        logger.info("Received message request: " + req.getTo());
        String fromUri = req.getFrom().getURI().toString();
        //Extract the URL
        String to = req.getTo().getURI().toString();
        String body = new String(req.getRawContent());
        int i = body.indexOf("<url>");
        int j = body.indexOf("</url>");
        String urlValue = body.substring(i + 5, j);
        if (urlValue.length() > 45) {
        String substrurlValue = urlValue.substring(0, 45);
        urlValue = substrurlValue + "........";
        }
        SipSession session = req.getSession();
        B2buaHelper helper = req.getB2buaHelper();
        logger.info(fromUri);
        String[] forwardingUri = forwardingUris.get(req.getTo().getURI().toString());

        if (blockedUris.contains(fromUri)) {
            logger.info(fromUri + " has been blocked !");

            //Send a 403 forbidden message
            SipServletResponse sipServletResponse = req.createResponse(SipServletResponse.SC_FORBIDDEN);
            sipServletResponse.send();
            logger.info("Keeping record of the session transfer request");
            //store the session transfer request
            sessiontracking sessiontransfer = new sessiontracking();
            sessiontransfer.storesessioninfo(fromUri, to, "MESSAGE", "Forbidden", urlValue);
        } else {

            if (forwardingUri != null && forwardingUri.length > 0) {

                SipFactory sipFactory = (SipFactory) getServletContext().getAttribute(
                        SIP_FACTORY);

                Map<String, List<String>> headers = new HashMap<String, List<String>>();
                List<String> toHeaderSet = new ArrayList<String>();
                toHeaderSet.add(forwardingUri[0]);
                headers.put("To", toHeaderSet);

                SipServletRequest forkedRequest = helper.createRequest(req, true,
                        headers);
                SipURI sipUri = (SipURI) sipFactory.createURI(forwardingUri[1]);
                forkedRequest.setRequestURI(sipUri);
                if (logger.isInfoEnabled()) {
                    logger.info("forkedRequest = " + forkedRequest);
                }
                forkedRequest.getSession().setAttribute("originalRequest", req);
                //Send the request on behalf of the source
                forkedRequest.send();
                //And send a 200 OK to the source
                SipServletResponse sipServletResponse = req.createResponse(SipServletResponse.SC_OK);
                sipServletResponse.send();
                logger.info("Keeping record of the session transfer request");
                //store the session transfer request
                sessiontracking sessiontransfer = new sessiontracking();
                sessiontransfer.storesessioninfo(fromUri, to, "MESSAGE", "Forwarded", urlValue);
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Web session has been proxied.");
                    //store the session transfer request
                    sessiontracking sessiontransfer = new sessiontracking();
                                        sessiontransfer.storesessioninfo(fromUri, to, "MESSAGE", "Proxied", urlValue);
                }
            }
        }
    }

    public void servletInitialized(SipServletContextEvent arg0) {
        logger.info("the Web session blocking sip servlet has been initialized");
    }
}
