package chatbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;  

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.awt.Desktop;

import javax.mail.*;
import javax.mail.internet.*;

public class Receptor extends Agent{

	final String urlWikipedia = "https://es.wikipedia.org/wiki/";
	final String rutaDatos = "src\\chatbot\\datos";
	final String memeRandomURL = "https://www.cuantocabron.com/aleatorio/";
	final String rutaMeme = "";

	String usuario = "";
	String contrasena = "";

	private class buscarPersona extends SimpleBehaviour{

		private boolean fin = false ;
        MessageTemplate plantilla = null;
		
		public void onStart(){
			
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("buscarPersona");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
			
			String [] credenciales = Leer(rutaDatos + "\\credenciales.txt");
			
			usuario = credenciales[0];
			contrasena = credenciales[1];
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
							
				String persona = parsearPersona(mensaje.getContent());
				
				if(persona.equals("")){
					
					Responder("Debes introducir el nombre de la persona a la que desees buscar!");
					
				}else{
					
					try{
					
						final Document document = Jsoup.connect(urlWikipedia+persona).get();
						
						
						if(!EncontrarPersona(document)){
							
							Responder("No he podido encontrarlo o encontrarla, lo siendo :c.");
							
						}
					
					}catch(Exception e){
					
						Responder("No he podido encontrarlo o encontrarla, lo siendo :c.");
					
					}
					
				}
			
			}
		
		}
		
		public boolean done(){
			
			return false;
			
		}
		
		public String parsearPersona(String cadena){
			
			String persona = ""; 
			String[] cadenas = cadena.split(" ");
			for(int i = 0; i<cadenas.length; i++){
				
				persona+=Character.toUpperCase(cadenas[i].charAt(0)) + cadenas[i].substring(1);
				if(!(i == cadenas.length-1)){
					
					persona+="_";
					
				}
			
			}
			
			return persona;
		
		}			
		
	}
	
	private class mostrarHora extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("mostrarHora");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			
			if(mensaje != null){
				
				String respuesta = "";
				
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				String fecha = dtf.format(LocalDateTime.now());
				
				respuesta+=ParsearFecha(fecha);
				
				Responder(respuesta);
			
			}
			
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
		public String ParsearFecha(String fecha){
			
			String[] fechaArray = fecha.split(" ");
			StringTokenizer f = new StringTokenizer(fechaArray[0],"/");  
			StringTokenizer h = new StringTokenizer(fechaArray[1],":");  
			String dia = "";
			String mes = "";
			String anio = "";
			String hora = "";
			String minuto = "";
			
			anio = f.nextToken();
			mes = f.nextToken();
			dia = f.nextToken();
			
			hora = h.nextToken();
			minuto = h.nextToken();
			
			return "Hoy es "+dia+" de " +ponerMes(mes)+ " de " +anio+ ". Son las "+hora+" y " +minuto+".";
			
		}
		
		public String ponerMes(String mes){
			
			switch(mes){
			
				case "01":
					return "enero";
				case "02":
					return "febrero";
				case "03":
					return "marzo";
				case "04":
					return "abril";
				case "05":
					return "mayo";
				case "06":
					return "junio";
				case "07":
					return "julio";
				case "08":
					return "agosto";
				case "09":
					return "septiembre";
				case "10":
					return "octubre";
				case "11":
					return "noviembre";
				case "12":
					return "diciembre";
				default:
					return "";
			
			}
			
		}
		
	}
	
	private class crearFichero extends SimpleBehaviour{
		
        MessageTemplate plantilla = null;
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("crearFichero");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
			
				String contenido = mensaje.getContent();
				String [] rutaFichero = contenido.split("/");
				String ruta = "";
				
				for(int i = 0; i<rutaFichero.length-1; i++){
				
					ruta+=rutaFichero[i];
				
				}
				
				File fichero = new File (ruta);
				try{
					if (new File(ruta).mkdir() && new File (contenido).createNewFile())
					
						Responder("El fichero: "+contenido+" ha sido creado con exito!");
					
					else
						
						Responder("El fichero: "+contenido+" no ha sido creado, lo siendo :(");
					
				}catch(IOException ioe){
				
					ResponderError(ioe.getMessage());
				
				}
			
			}
		
		}
		
		public boolean done(){
			
			return false;
			
		}
				
	}
	
	private class borrarFichero extends SimpleBehaviour{
		
        MessageTemplate plantilla = null;
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("borrarFichero");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
			
				String contenido = mensaje.getContent();
				
				File fichero = new File (contenido);
				
				if(fichero.delete())
				
					Responder("El fichero "+contenido+ " ha sido eliminado con éxito.");
				
				else
					
					ResponderError("El fichero: "+contenido+" no ha sido eliminado, lo siendo :(");
					
			}
		
		}
		
		public boolean done(){
			
			return false;
			
		}
				
	}
	
	private class terminarEjecucion extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("terminarEjecucion");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
			
				String contenido = mensaje.getContent();
				Responder("Hasta la vista!");
				fin = true;
				block(20);
				
			}
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
		public int onEnd(){
		
			doDelete();
			return super.onEnd();
		
		}
		
	}
	
	private class recomendarJuego extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;

		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("recomendarJuego");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){

			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
			
				String[] ficheros =	encontrarFicheros(rutaDatos+"\\Juegos");
				String[] juegos = Leer(rutaDatos+"\\Juegos\\"+ficheros[(int)(Math.random() * ficheros.length)]);
				try{
					
					final Document document = Jsoup.connect(urlWikipedia+juegos[(int)(Math.random() * juegos.length)]).get();
					EncontrarJuego(document);

				} catch(Exception e){}
				
				
			}
			
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
	}
	
	private class enviarCorreo extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		private Scanner sc = new Scanner(System.in);
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("enviarCorreo");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);

			if(mensaje != null){
				
				boolean datosCorrectos = false;
				boolean enviarMensaje = true;
				String [] datos = null;
				System.out.print("Bot: Claro! Pero necesitare algunos datos. Escribe los datos y separalos por '///'. Si ya no quieres mandar un correo, no escribas nada y dale al intro! Aqui te muestro un ejemplo:\n\n\tprueba@gmail.com///reunion///Hola buenas, necesito reunirme contigo, gracias.\n\n\t1) Quien va a ser el afortunado o la afortunada que va a recibir tu email?\n\t2) Cual quieres que sea el asunto del correo?\n\t3) Por ultimo, que mensaje quieres trasmitirle?\nTu: ");
				
				do{
					
					String cadenaDatos = sc.nextLine();
					datos = cadenaDatos.split("///");
					
					if(datos.length == 3){
						
						datosCorrectos = true;
						break;
						
					}else if(cadenaDatos.equals("")){
					
						enviarMensaje = false;
						break;
					
					}
					
					System.out.print("Bot: el formato en el que has metido los datos no es correcto, o prueba con este ejemplo:\n\n\tprueba@gmail.com///reunion///Hola buenas, necesito reunirme contigo, gracias.\nBot: Si te has arrepentido y no quieres mandar el correo no pasa nada, dale al intro y ya!\nTu:");
					
				}while(!datosCorrectos);
					
				if(enviarMensaje){
					
					String emisorCorreo = "chatbotpablo@gmail.com";
					String contrasena = "chatbotPablo1";
					String receptorCorreo = datos[0];
					String asunto = datos[1];
					String contenido = datos[2];

					Properties properties = System.getProperties();
					properties.put("mail.smtp.host", "smtp.gmail.com");
					properties.put("mail.smtp.port", 465);
					properties.put("mail.smtp.socketFactory.port", 465);
					properties.put("mail.smtp.auth", "true");
					properties.put("mail.smtp.starttls.enable", "true");
					properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
					properties.put("mail.smtp.debug", "true");
					properties.put("mail.smtp.socketFactory.fallback", "false");
					properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

					Session sesion = Session.getDefaultInstance(properties);

					MimeMessage mail = new MimeMessage(sesion);

					try{

						mail.setFrom(new InternetAddress(usuario));
						mail.addRecipient(Message.RecipientType.TO, new InternetAddress(receptorCorreo));
						mail.setSubject(asunto);
						mail.setText(contenido);

						Transport transporte = sesion.getTransport("smtp");

						transporte.connect(usuario, contrasena);
						transporte.sendMessage(mail, mail.getRecipients(Message.RecipientType.TO));
						transporte.close();

						Responder("El correo a "+datos[0].split("@")[0]+" con el asunto de " +datos[1]+ " ha sido enviado correctamente!");

					} catch(AddressException e)  {Responder("El formato de la direccion no es correcto :/\nBot: Cancelar enviar correo");}
					  catch(MessagingException e)  {ResponderError(e.getMessage() + "\nBot: Cancelar enviar correo");}

				} else{

					Responder("Cancelar enviar correo.");

				}
				
			}
			
		}
	
		public boolean done(){
			
			return fin;
			
		}
		
		
	}
	
	private class memeRandom extends SimpleBehaviour{

		private boolean fin = false ;
        MessageTemplate plantilla = null;

		public void onStart(){

			AID emisor = new AID ();
            emisor.setLocalName("emisor");

            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("memeRandom");

            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );

		}

		public void action (){

			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){

				try{

					Document document = Jsoup.connect(memeRandomURL).get();
					Element imageElement = document.select("#main > div:nth-child(1) > div:nth-child(1) > p > span > a > img").first();

					String strImageURL = imageElement.attr("abs:src");
					descargarIMG(strImageURL);

				} catch(Exception e){}

			}

		}

		public boolean done(){
			
			return fin;
			
		}
		
	}	
	private class saludos extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		private String [] salu2 = { "hola, que tal", "holaa :)", "holi", "buenas" };
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("saludos");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
				
				Responder(salu2[ (int) Math.floor(Math.random() * salu2.length) ]);

			}
		
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
		
	}

	private class preguntas extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		private String[] resp = { "bien, gracias", "regular, gracias", "la verdad es que muy mal :c" };
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("preguntas");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje!=null){
				
				Responder(resp[ (int) Math.floor(Math.random() * resp.length) ]);

			}
		
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
		
	}
	
	private class respuestas extends SimpleBehaviour{
		
		private boolean fin = false ;
        MessageTemplate plantilla = null;
		
		public void onStart(){
		
			AID emisor = new AID ();
            emisor.setLocalName("emisor");
            
            MessageTemplate filtroEmisor = MessageTemplate.MatchSender(emisor);
            MessageTemplate filtroInform = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            MessageTemplate filtroIdioma =  MessageTemplate.MatchLanguage ("Spanish");
            MessageTemplate filtroProtocolo =  MessageTemplate.MatchProtocol ("respuestas");
            
            plantilla = MessageTemplate.and(filtroInform,filtroEmisor);
            plantilla = MessageTemplate.and(plantilla,filtroIdioma );
            plantilla = MessageTemplate.and(plantilla,filtroProtocolo );
		
		}
		
		public void action (){
		
			ACLMessage mensaje = receive(plantilla);
			if(mensaje != null){
				
				Responder("Quieres algo?");

			}
		
		}
		
		public boolean done(){
			
			return fin;
			
		}
		
		
	}

	protected void setup() {
		
		addBehaviour(new buscarPersona());
		addBehaviour(new mostrarHora());
		addBehaviour(new crearFichero());
		addBehaviour(new borrarFichero());
		addBehaviour(new terminarEjecucion());
		addBehaviour(new recomendarJuego());
		addBehaviour(new memeRandom());
		addBehaviour(new enviarCorreo());
		addBehaviour(new saludos());
		addBehaviour(new preguntas());
		addBehaviour(new respuestas());
	
	}

	protected void takeDown(){
		
		System.out.println("El funcionamiento del receptor ha terminado!");
		
	} 
	
	protected void Responder(String content){
		
		ACLMessage respuesta = new ACLMessage (ACLMessage.INFORM);
		
		respuesta.setSender(getAID());
		respuesta.setLanguage("Spanish");
		
		AID id = new AID();
		id.setLocalName("emisor");
		
		respuesta.addReceiver(id);
		respuesta.setContent("Bot: " + content);
		
		send(respuesta);
		
	}
		
	protected void ResponderError(String content){
		
		ACLMessage respuesta = new ACLMessage (ACLMessage.FAILURE);
		
		respuesta.setSender(getAID());
		respuesta.setLanguage("Spanish");
		
		AID id = new AID();
		id.setLocalName("emisor");
		
		respuesta.addReceiver(id);
		respuesta.setContent("Bot: " + content);
		
		send(respuesta);
		
	}

	protected boolean EncontrarPersona(Document document){
		
		try{
			
			for(int i = 0; i < 10; i++){
					
				String cadena = document.select("#mw-content-text > div.mw-parser-output > p:nth-child("+i+")").text();
				if(!cadena.equals("")){
					
					Responder(limpiarCadena(cadena));
					return true;

				}

			}

			return false;

		}catch(Exception e){

			return false;				

		}

	}

	protected void EncontrarJuego(Document document){
		
		try{
			
			String cadena = "";
			
			for(int i = 0; i < 10; i++){
					
				cadena = document.select("#mw-content-text > div.mw-parser-output > p:nth-child("+i+")").text();
				if(!cadena.equals("")){
					
					cadena += "\n\n";
					break;
					
				}
				
			}
			cadena.replace(".", ".\n");
			cadena+="--------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n";
			for(int i=0; i<20; i++){
			
				String aux = document.select("#mw-content-text > div.mw-parser-output > table.infobox.plainlist.plainlinks > tbody > tr:nth-child("+i+")").text();
				
				if(!aux.equals("")){
					
					if(!(aux.charAt(0) == '[')){
					
						cadena+=  "\t- "+aux+"\n\n";
					
					}else{
						
						break;
						
					}
	
				}
				
			}
			
			Responder(limpiarCadena(cadena));
			
		}catch(Exception e){
			
			ResponderError(e.getMessage());
			
		}
	
	}

	protected String[] Leer(String ruta){
	
		File fichero = new File(ruta);
		Scanner s = null;
		List<String> lista = new ArrayList<String>();
		
		try {
			
			s = new Scanner(fichero);

			while (s.hasNextLine()) {
			
				String linea = s.nextLine();
				lista.add(linea);
			}

		} catch (Exception ex) {
		
			ResponderError(ex.getMessage());
		
		} finally {
			
			try {
			
				if (s != null)
					s.close();

			} catch (Exception ex2) {

				ResponderError(ex2.getMessage());

			}

		}

		Object [] o = lista.toArray();

		return Arrays.copyOf(o, o.length, String[].class);

	}

	protected String[] encontrarFicheros(String ruta) {

		File folder = new File(ruta);
		
		String ficheros = "";
		
		for (File file : folder.listFiles()) {
		
			if (!file.isDirectory()) {
			
				ficheros+=file.getName()+";";
		
			}
		
		}
		
		return ficheros.split(";");
		
	}
	
	protected String limpiarCadena(String cadena){
	
		
		for(int i = 1; i <= 30; i++){
			
			cadena = cadena.replace("["+i+"]","");
			
		}

		return cadena;
	
	}
	
	protected void descargarIMG(String strImageURL){
        
        String strImageName = strImageURL.substring( strImageURL.lastIndexOf("/") + 1 );
                
        try {
            
            URL urlImage = new URL(strImageURL);
            InputStream in = urlImage.openStream();
            
            byte[] buffer = new byte[4096];
            int n = -1;
            
            OutputStream os = new FileOutputStream( rutaMeme+"meme.png" );
            
            while ( (n = in.read(buffer)) != -1 ){
                os.write(buffer, 0, n);
            }           
            
			os.close();
			
			File meme = new File(rutaMeme+"meme.png");
			Desktop d  = Desktop.getDesktop();
			d.open(meme); 

			Responder("Espero que te haya gustado!");
            
        } catch (IOException e) {
			
            ResponderError(e.getMessage());
       
	   }
        
    }

}