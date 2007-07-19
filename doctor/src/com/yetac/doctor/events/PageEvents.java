package com.yetac.doctor.events;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.yetac.doctor.workers.Files;

/**
 * Page events to provide headers and footers
 */

public class PageEvents extends PdfPageEventHelper 
{
	Files _files;
	
	public PageEvents(Files files) {
		_files = files;
	}
	
	// we override the onStartPage method
	public void onStartPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
        PdfTemplate template = cb.createTemplate(600, 38);
        try {
        	Image jpg = Image.getInstance(_files.task.inputImages() + "/db4objects.gif");
        	jpg.setAbsolutePosition(430, 0);
        	template.addImage(jpg);
        } catch (MalformedURLException e) {
        	_files.task.log("db4objects logo could not be found: " + e.toString());
        } catch (IOException e) {
        	_files.task.log("db4objects logo could not be found: " + e.toString());
        } catch (DocumentException e) {
        	_files.task.log("db4objects logo could not be added");
        }
        // unfortunately the next line does not work with the old version of iText
		// template.setAction(new PdfAction("http://www.db4o.com"), 380, 750, 570, 795);
		cb.addTemplate(template, 0, 750);
	}
}