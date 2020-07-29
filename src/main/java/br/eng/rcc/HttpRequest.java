package br.eng.rcc;

import java.util.Map;

public class HttpRequest {
    
    public String version;
    public String method;
    public String path;
    public Map<String, String> headers;
    public String bodyRaw;

    public String header(String key) {
        return this.headers.get( key.toLowerCase() );
    }

    // -------------------------------
    // para os cabeçalhos: "Accept-Encoding" e "Content-Encoding"
    public static enum Encoding {
        GZIP("gzip"),
        X_GZIP("gzip"),
        COMPRESS("compress"),
        X_COMPRESS("compress"),
        DEFLATE("deflate"),
        IDENTITY("identity"), // não deve ser usado no "Content-Encoding"!
        CHUNKED("chunked"), // apenas para cabeçalho "Transfer-Coding"
        ;
        public final String name;
        private Encoding(String name) {
            this.name = name;
        }
    }

}