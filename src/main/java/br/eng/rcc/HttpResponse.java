package br.eng.rcc;

import java.io.IOException;
import java.io.Writer;

public class HttpResponse {
    
    public int status;
    public Object resposta;
    public String contentType;

    public HttpResponse() {
        
    }
    public HttpResponse(int status, Object resposta) {
        this.status = status;
        this.resposta = resposta;
    }

    public void writeTo(Writer out) throws IOException {
        out.write(String.format("HTTP/1.1 %d OK\r\n", status, statusMsg()));
        out.write("\r\n"); // fim do cabecalho e inicio d corpo
        if( resposta instanceof String ) {
            out.write( resposta.toString() );
        }
    }

    private String statusMsg() {
        switch(status) {
            case 200: return "OK";
        }
        return ".";
    }

}