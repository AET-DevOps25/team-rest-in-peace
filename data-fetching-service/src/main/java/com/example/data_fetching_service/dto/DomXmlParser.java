package com.example.data_fetching_service.dto;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DomXmlParser {

    public static PlenaryProtocolXml parsePlenarprotokoll(String xmlContent) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // Set an EntityResolver to handle the DTD from the classpath
        dBuilder.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                if (systemId.endsWith("dbtplenarprotokoll.dtd")) {
                    // Load the DTD from the classpath (resources folder)
                    InputStream dtdStream = getClass().getClassLoader().getResourceAsStream("dbtplenarprotokoll.dtd");
                    if (dtdStream != null) {
                        return new InputSource(dtdStream);
                    } else {
                        System.err.println("DTD file not found in resources: dbtplenarprotokoll.dtd");
                        // Fallback to null to tell the parser to try and resolve it normally (which will likely fail)
                        return null;
                    }
                }
                // For other entities, let the default resolver handle them
                return null;
            }
        });

        InputSource is = new InputSource(new StringReader(xmlContent));
        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        PlenaryProtocolXml plenaryProtocolXml = new PlenaryProtocolXml();
        PlenaryProtocolXml.Sitzungsverlauf sitzungsverlaufObj = new PlenaryProtocolXml.Sitzungsverlauf();
        plenaryProtocolXml.setSitzungsverlauf(sitzungsverlaufObj);
        sitzungsverlaufObj.setTagesordnungspunkte(new ArrayList<>());

        NodeList sitzungsverlaufNodes = doc.getElementsByTagName("sitzungsverlauf");
        if (sitzungsverlaufNodes.getLength() == 1) {
            Element sitzungsverlaufElement = (Element) sitzungsverlaufNodes.item(0);

            // Sitzungsbeginn
            NodeList sitzungsbeginnNodes = sitzungsverlaufElement.getElementsByTagName("sitzungsbeginn");
            if (sitzungsbeginnNodes.getLength() > 0) {
                Element sitzungsbeginnElement = (Element) sitzungsbeginnNodes.item(0);
                sitzungsverlaufObj.setSitzungsbeginn(sitzungsbeginnElement.getTextContent());
            }

            // Sitzungsende
            NodeList sitzungsendeNodes = sitzungsverlaufElement.getElementsByTagName("sitzungsende");
            if (sitzungsendeNodes.getLength() > 0) {
                Element sitzungsendeElement = (Element) sitzungsendeNodes.item(0);
                sitzungsverlaufObj.setSitzungsende(sitzungsendeElement.getTextContent());
            }

            // Tagesordnungspunkte
            NodeList topNodes = sitzungsverlaufElement.getElementsByTagName("tagesordnungspunkt");
            for (int i = 0; i < topNodes.getLength(); i++) {
                if (topNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element topElement = (Element) topNodes.item(i);
                    PlenaryProtocolXml.Tagesordnungspunkt top = new PlenaryProtocolXml.Tagesordnungspunkt();
                    top.setTopId(topElement.getAttribute("top-id"));
                    top.setReden(new ArrayList<>());
                    top.setParagraphs(new ArrayList<>());
                    top.setKommentare(new ArrayList<>());

                    // Reden within TOP
                    NodeList redenNodes = topElement.getElementsByTagName("rede");
                    for (int j = 0; j < redenNodes.getLength(); j++) {
                        if (redenNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element redeElement = (Element) redenNodes.item(j);
                            PlenaryProtocolXml.Rede rede = new PlenaryProtocolXml.Rede();
                            rede.setId(redeElement.getAttribute("id"));
                            rede.setInhalte(new ArrayList<>());

                            // Redner within Rede
                            NodeList rednerNodes = redeElement.getElementsByTagName("redner");
                            if (rednerNodes.getLength() > 0) {
                                Element rednerElement = (Element) rednerNodes.item(0);
                                PlenaryProtocolXml.Redner redner = new PlenaryProtocolXml.Redner();
                                redner.setId(rednerElement.getAttribute("id"));
                                NodeList nameNodes = rednerElement.getElementsByTagName("name");
                                if (nameNodes.getLength() > 0) {
                                    Element nameElement = (Element) nameNodes.item(0);
                                    PlenaryProtocolXml.Name name = new PlenaryProtocolXml.Name();
                                    NodeList titelNodes = nameElement.getElementsByTagName("titel");
                                    if (titelNodes.getLength() > 0) name.setTitel(titelNodes.item(0).getTextContent());
                                    NodeList vornameNodes = nameElement.getElementsByTagName("vorname");
                                    if (vornameNodes.getLength() > 0)
                                        name.setVorname(vornameNodes.item(0).getTextContent());
                                    NodeList nachnameNodes = nameElement.getElementsByTagName("nachname");
                                    if (nachnameNodes.getLength() > 0)
                                        name.setNachname(nachnameNodes.item(0).getTextContent());
                                    NodeList fraktionNodes = nameElement.getElementsByTagName("fraktion");
                                    if (fraktionNodes.getLength() > 0)
                                        name.setFraktion(fraktionNodes.item(0).getTextContent());
                                    redner.setName(name);
                                }
                                rede.setRedner(redner);
                            }

                            NodeList redeChildNodes = redeElement.getChildNodes();
                            for (int k = 0; k < redeChildNodes.getLength(); k++) {
                                Node childNode = redeChildNodes.item(k);
                                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element childElement = (Element) childNode;
                                    String tagName = childElement.getTagName();
                                    switch (tagName) {
                                        case "p":
                                            PlenaryProtocolXml.SpeechParagraph speechParagraph = new PlenaryProtocolXml.SpeechParagraph();
                                            String pKlasse = childElement.getAttribute("klasse");
                                            if (pKlasse.equals("redner")) {
                                                break;
                                            }
                                            speechParagraph.setKlasse(pKlasse);
                                            speechParagraph.setText(childElement.getTextContent());
                                            rede.getInhalte().add(speechParagraph);
                                            break;
                                        case "kommentar":
                                            PlenaryProtocolXml.Kommentar kommentar = new PlenaryProtocolXml.Kommentar();
                                            kommentar.setText(childElement.getTextContent());
                                            rede.getInhalte().add(kommentar);
                                            break;
//                                        case "zitat":
//                                            PlenaryProtocolXml.Zitat zitat = new PlenaryProtocolXml.Zitat();
//                                            zitat.setText(childElement.getTextContent());
//                                            rede.getInhalte().add(zitat);
//                                            break;
                                        // Handle other potential child elements of <rede> if needed
                                    }
                                }
                            }

                            top.getReden().add(rede);
                        }
                    }

                    // Paragraphs directly under TOP (not within Rede)
                    NodeList paragraphNodes = topElement.getElementsByTagName("p");
                    for (int j = 0; j < paragraphNodes.getLength(); j++) {
                        if (paragraphNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element pElement = (Element) paragraphNodes.item(j);
                            PlenaryProtocolXml.Paragraph paragraph = new PlenaryProtocolXml.Paragraph();
                            paragraph.setKlasse(pElement.getAttribute("klasse"));
                            paragraph.setText(pElement.getTextContent());
                            top.getParagraphs().add(paragraph);
                        }
                    }

                    // Kommentare directly under TOP (not within Rede)
                    NodeList topKommentarNodes = topElement.getElementsByTagName("kommentar");
                    for (int j = 0; j < topKommentarNodes.getLength(); j++) {
                        if (topKommentarNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element kommentarElement = (Element) topKommentarNodes.item(j);
                            PlenaryProtocolXml.Kommentar kommentar = new PlenaryProtocolXml.Kommentar();
                            kommentar.setText(kommentarElement.getTextContent());
                            top.getKommentare().add(kommentar);
                        }
                    }

                    sitzungsverlaufObj.getTagesordnungspunkte().add(top);
                }
            }

            // Text content of sitzungsverlauf itself
            StringBuilder sitzungsverlaufText = new StringBuilder();
            NodeList childNodes = sitzungsverlaufElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.TEXT_NODE) {
                    sitzungsverlaufText.append(node.getTextContent().trim());
                }
            }
            if (!sitzungsverlaufText.isEmpty()) {
                sitzungsverlaufObj.setText(sitzungsverlaufText.toString());
            }
        }

        return plenaryProtocolXml;
    }
}