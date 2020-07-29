package br.eng.rcc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class HttpParserTest {

    @Test
    public void test1() throws IOException {
        HttpRequest req = HttpParser.parse( HttpParserTest.class.getClassLoader().getResourceAsStream("arquivo_post1.http") );
        assertEquals("POST", req.method);
        assertEquals("/endereco/arquivo?param1=valor1", req.path);
        assertEquals("HTTP/1.1", req.version);

        assertEquals("www.never.com", req.header("Host") );
        assertEquals("text/plain", req.header("Content-Type") );
        assertEquals("identity", req.header("accept-encoding") );

        assertEquals("Novo endereço: Rua de Trás", req.bodyRaw );
    }

}