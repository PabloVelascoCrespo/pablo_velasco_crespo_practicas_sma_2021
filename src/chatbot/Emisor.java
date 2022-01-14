package chatbot;

import jade.core.Agent; 
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Scanner;


public class Emisor extends Agent{ 

	private class Comportamiento extends SimpleBehaviour{

		private String[] buscarPersona = 		{"quien es", "que hizo", "a que se dedica", "puedes decirme quien es", "sabes quien es", "podrias decirme quien es", "que fue lo que hizo"};
		private String[] mostrarHora = 			{"que hora es", "me puedes decir la hora", "dime la hora", "me puedes decir que hora es", "hora"};
		private String[] crearFichero = 		{"crea un fichero", "crea un archivo", "creame un fichero", "creame un archivo", "me puedes crear un fichero", "me puedes crear un archivo"};
		private String[] borrarFichero = 		{"elimina el fichero", "elimina el archivo", "me puedes eliminar el fichero", "me puedes eliminar el archivo", "borra el fichero", "borra el archivo", "me puedes borrar el fichero", "me puedes borrar el archivo"};
		private String[] terminarEjecucion = 	{"adios", "hasta luego", "nos vemos","apagar", "apagate", "terminar ejecucion", "hasta la proxima", "buenas noches", "me voy", "me tengo que ir", "debo irme"};
		private String[] memeRandom = 			{"meme", "enseñame un meme", "estoy triste", "animame", "hazme reir", "necesito reirme", "me enseñas un meme", "puedes enseñarme un meme", "mandame un meme"};
		private String[] recomendarJuego = 		{"juego", "juegos", "videojuego", "videojuegos", "recomiendame un juego", "me recomiendas un juego", "me recomiendas un juego", "dime un juego", "recomiendame un videojuego", "recomiendame un videojuego", "dime un videojuego"};
		private String[] enviarCorreo = 		{"puedes enviar un correo","necesito mandar un correo","manda un correo", "necesito enviar un correo", "correo"};
		private String[] saludos = 				{"hola", "buenas tardes", "buenos dias", "buenas", "holi"};
		private String[] preguntas = 			{"que tal", "como estas", "estas bien", "como andas", "como te encuentas", "te encuentras bien"};
		private String[] respuestas = 			{"me alegro", "me alegra oir eso", "muy bien", "guay", "bien", "espectacular"};
		private String[][] lista = {buscarPersona, mostrarHora, crearFichero, borrarFichero, terminarEjecucion, memeRandom, recomendarJuego, enviarCorreo, saludos, preguntas, respuestas};

		private boolean fin = false;
		private boolean enviado = false;
		private Scanner sc = new Scanner(System.in);
		private ACLMessage mensaje = new ACLMessage ( ACLMessage.REQUEST );

		public void onStart(){

			System.out.println("Bot: Hola!");
			mensaje.setSender(getAID());
			mensaje.setLanguage("Spanish");
			AID id = new AID();
            id.setLocalName("receptor");
			mensaje.addReceiver(id);

		}

        public void action(){

			System.out.print("Tu: ");

			try{

				String entrada = sc.nextLine();
				Clasificar( entrada );

			}catch(Exception e){

				System.out.println("Bot: Lo siento no te he entendido, puedes repetirlo?");

			}

			send(mensaje);

			if(enviado){

				ACLMessage respuesta = blockingReceive();
				String contenido = respuesta.getContent();

				switch(respuesta.getPerformative()){

					case ACLMessage.INFORM:
						System.out.println(contenido);
						break;

					case ACLMessage.FAILURE:
						System.out.println("Bot: Ha ocurrido un problema, te muestro el mensaje del error.\n"+contenido);
						break;

				}

				if(contenido.equals("Bot: Hasta la vista!")){

					fin = true;

				}

				mensaje.setContent("");
				mensaje.setProtocol(null);

			}

		}

		public boolean done(){

			return fin;

		}

		public int onEnd(){

			doDelete();
			return super.onEnd();

		}

		public void Clasificar( String entrada ){

			entrada = entrada.trim();
			entrada = entrada.replaceAll("\\s+", " ");
			entrada = entrada.toLowerCase();

			String [] entradaArray = entrada.split(" ");

			for(int i = 0; i < lista.length; i++){

				if(Comparar(lista[i], entradaArray)){

					mensaje.setProtocol(SeleccionarProtocolo(i));
					break;

				}

			}


			if(mensaje.getProtocol() == null){

				System.out.println("Bot: Lo siento no te he entendido, puedes repetirlo?");
				enviado = false;

			}

		}

		public boolean Comparar(String[] cadena, String[] entrada){

			for(int i = 0; i < cadena.length; i++){

				String [] aux = cadena[i].split(" ");
				int cont = 0;

				for(int j = 0; j<aux.length; j++){

					if(aux[j].equals(entrada[j])){

						cont++;

					}else{

						break;

					}

				}

				if(cont == aux.length){

					String msg = "";

					for(int j = cont; j<entrada.length; j++){

						msg += entrada[j]+ " ";

					}

					mensaje.setContent(msg);
					enviado = true;
					return true;

				}

			}

			return false;

		}

		public String SeleccionarProtocolo(int num){
	
			switch(num){

				case 0: return "buscarPersona";
				case 1: return "mostrarHora";
				case 2: return "crearFichero";
				case 3: return "borrarFichero";
				case 4: return "terminarEjecucion";
				case 5: return "memeRandom";
				case 6: return "recomendarJuego";
				case 7: return "enviarCorreo";	
				case 8: return "saludos";
				case 9: return "preguntas";
				case 10: return "respuestas";
				default: return "";

			}

		}

	}

	protected void setup() {

		addBehaviour(new Comportamiento());

	}

	protected void takeDown(){

		System.out.println("\nEl funcionamiendo del emisor ha terminado!"); 

	} 

}