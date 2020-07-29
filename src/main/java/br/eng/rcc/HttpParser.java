package br.eng.rcc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.IntPredicate;

/**
 * Classe para interpretar protocolo HTTP (apenas leitura do texto).
 * 
 * Especificação HTTP 1.1:
 * https://tools.ietf.org/html/rfc2616#section-3.5
 * 
 * Tabela ASCII:
 * https://upload.wikimedia.org/wikipedia/commons/c/cf/USASCII_code_chart.png
 */
public class HttpParser {
    private static final int END_NUM = -2;
    private static final int END_STREAM = -1;
    
    public static HttpRequest parse(InputStream input) throws IOException {
        return parseHttp( input );
    }
    public static HttpRequest parse(byte[] input) throws IOException {
        return parseHttp( new ByteArrayInputStream(input) );
    }
    public static HttpRequest parse(String input) throws IOException {
        return parseHttp( new ByteArrayInputStream(input.getBytes("UTF-8")) );
    }

    // ================================================
    // ================================================
    // ================================================

    private static HttpRequest parseHttp(InputStream in) throws IOException {
        BufferedInputStream reader;
        if( in instanceof BufferedInputStream ) {
            reader = (BufferedInputStream) in;
        } else {
            reader = new BufferedInputStream( in );
        }

        HttpRequest req = new HttpRequest();
        req.headers = new HashMap<>();
        parseFirstLine(req, reader);
        parseHeaders(req, reader);
        parseBody(req, reader);

        return req;
    }
    private static void parseFirstLine(HttpRequest req, BufferedInputStream reader) throws IOException {
        req.method = readString( reader );
        readSpace( reader );
        req.path = readString( reader );
        readSpace( reader );
        req.version = readString( reader );
        readSpace( reader );
        readLine( reader );
    }
    private static void parseHeaders(HttpRequest req, BufferedInputStream reader) throws IOException {
        boolean lendoHeaders = true;
        while( lendoHeaders ) {
            String nome = readToken( reader );
            readSpace( reader );
            String sep = readSeparator( reader );
            readSpace( reader );
            String valor = readString( reader ); // ALTERAR!!! necessário criar metodo "readText()"!!!
            readSpace( reader );
            
            if( !nome.isEmpty() ) {
                req.headers.put( nome.toLowerCase(), valor );
            }
            if( readLine( reader ).length >= 4 || ( nome.isEmpty() && sep.isEmpty() && valor.isEmpty() ) ) {
                lendoHeaders = false;
            }
        }
        readSpace( reader );
        readLine( reader );
    }
    private static void parseBody(HttpRequest req, BufferedInputStream reader) throws IOException {
        // NECESSARIO REVER IMPL.!!!
        ByteArrayOutputStream sb = new ByteArrayOutputStream( 2048 );
        byte[] buf = new byte[2048];
        int len = 0;
        while( (len = reader.read( buf )) > 0 ) {
            sb.write( buf, 0, len );
        }
        req.bodyRaw = sb.toString("UTF-8");
    }

    // ================================================
    // ================================================
    // ================================================

    private static String readString(BufferedInputStream reader) throws IOException {
        return new String( readBytes( reader, item -> ( !is_CTL(item) && !is_SP(item) ) ) ) ;
    }
    private static String readToken(BufferedInputStream reader) throws IOException {
        return new String( readBytes( reader, item -> ( is_CHAR(item) && !is_CTL(item) && !is_Separator(item) ) ) ) ;
    }
    private static String readSeparator(BufferedInputStream reader) throws IOException {
        return new String( readBytes( reader, item -> ( is_Separator(item) ) ) ) ;
    }
    private static String readText(BufferedInputStream reader) throws IOException {
        // return new String( readBytes( reader, item -> ( !is_CTL(item) && !is_SP(item) ) ) ) ;
        return null;
    }
    private static byte[] readSpace(BufferedInputStream reader) throws IOException {
        return readBytes( reader, item -> ( is_SP(item) ) );
    }
    private static byte[] readLine(BufferedInputStream reader) throws IOException {
        return readBytes( reader, item -> ( is_CR(item) || is_LF(item) ) );
    }

    
    private static byte[] readBytes(BufferedInputStream reader, IntPredicate pred) throws IOException {
        reader.mark(1000);
        int contagem = 0;
        int item;
        while( (item = reader.read()) >= 0 && pred.test(item) ) contagem++ ;
        reader.reset();
        byte[] buf = new byte[ contagem ];
        reader.read( buf );
        // System.out.println("--- Len: " + contagem);
        return buf;
    }


    // -----------

    private static boolean is_OCTET(int data)  {
        return data < 256 ;
    }
    private static boolean is_CHAR(int data) {
        return data < 128 ;
    }
    private static boolean is_UPALPHA(int data) {
        return ( data >= 0x41 && data <= 0x5A );
    }
    private static boolean is_LOALPHA(int data) {
        return ( data >= 0x61 && data <= 0x7A );
    }
    private static boolean is_ALPHA(int data) {
        return is_UPALPHA(data) || is_LOALPHA(data) ;
    }
    private static boolean is_DIGIT(int data) {
        return ( data >= 0x30 && data <= 0x39 ) ;
    }
    private static boolean is_CTL(int data) {
        return data < 32;
    }
    private static boolean is_CR(int data) {
        return data == 13;
    }
    private static boolean is_LF(int data) {
        return data == 10;
    }
    private static boolean is_SP(int data) {
        return data == 32;
    }
    private static boolean is_HT(int data) {
        return data == 9;
    }
    private static boolean is_DoubleQuote(int data) {
        return data == 34;
    }
    private static boolean is_Separator(int data) {
        return is_SP(data) || is_HT(data) || is_DoubleQuote(data) 
            || data == 0x28 // (
            || data == 0x29 // )
            || data == 0x2C // ,
            || data == 0x3C // <
            || data == 0x3E // >
            || data == 0x40 // @
            || data == 0x3A // :
            || data == 0x3B // ;
            || data == 0x2F // /
            || data == 0x5B // [
            || data == 0x5C // \
            || data == 0x5D // ]
            || data == 0x7B // {
            || data == 0x7D // }
            || data == 0x3D // =
            || data == 0x3F // ?
            ;
    }

}