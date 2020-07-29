package br.eng.rcc;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger log = Logger.getLogger(App.class.getCanonicalName());

    public static void main(String[] args) {
        log.log(Level.INFO, "Iniciando... ");
        Properties props = carregarConfig();
        Servidor servidor = new Servidor();
        try {
            servidor.iniciar( 8080 );
        } catch (IOException e) {
            log.log(Level.SEVERE, "Erro durante execução do servidor!", e);
        }
    }

    private static Properties carregarConfig() {
        Properties props = new Properties();

        File arquivo = new File("application.properties");
        if (arquivo.exists()) {
            try {
                props.load(new FileReader(arquivo));
            } catch (IOException e) {
                log.log(Level.WARNING, "Erro ao carregar arquivo de configuracoes!", e);
            }
        }
        
        return props;
    }

}