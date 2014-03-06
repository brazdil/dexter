package uk.ac.cam.db538.dexter.aux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.HttpEntity;

public class TaintHelper {

    public static String getHttpUriRequestDetails(HttpUriRequest request) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(request.getMethod());
        sb.append(' ');
        sb.append(request.getURI());
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = ((HttpEntityEnclosingRequestBase)request).getEntity();
            if (entity != null) {
                try {
                    sb.append(' ');
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String inputLine;
                      while ((inputLine = in.readLine()) != null) {
                          sb.append(inputLine);
                          sb.append(' ');
                      }
                      in.close();
                 } catch (IOException e) {
                 }
            }
        }
        return sb.toString();
    }
}
