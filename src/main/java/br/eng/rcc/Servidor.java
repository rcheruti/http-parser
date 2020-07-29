package br.eng.rcc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static final Logger log = Logger.getLogger(Servidor.class.getCanonicalName());

    private ExecutorService threads;
    private ServerSocket server;

    // ------------------------

    public void iniciar(int port) throws IOException {
        threads = Executors.newCachedThreadPool();
        server = new ServerSocket(port);
        log.log(Level.INFO, "Servidor ouvindo na porta: " + server.getLocalPort() );

        for(;;) {
            Socket socketReq = server.accept();
            threads.execute( new Requisicao( socketReq ) );
        }
    }
    
    // ------------------------

    public static class Requisicao implements Runnable {
        private static final Logger log = Logger.getLogger(Requisicao.class.getCanonicalName());

        private Socket socketReq;

        public Requisicao(Socket socketReq) {
            this.socketReq = socketReq;
        }

        public void run() {
            try {
                HttpRequest req = HttpParser.parse(socketReq.getInputStream());
                OutputStream out = socketReq.getOutputStream();

                Writer writer = new OutputStreamWriter( out );
                new HttpResponse( 200, "Arquivo informado: " + req.path ).writeTo( writer );
                writer.flush();
                writer.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Erro ao processar requisição: ", e);
            }
            try {
                socketReq.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Erro ao finalizar requisição: ", e);
            }
        }
    }

}