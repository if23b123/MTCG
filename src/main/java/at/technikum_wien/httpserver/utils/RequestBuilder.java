package at.technikum_wien.httpserver.utils;
import at.technikum_wien.httpserver.server.Request;
import at.technikum_wien.httpserver.http.Method;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;


public class RequestBuilder {
    public Request buildRequest(BufferedReader buffer) throws IOException {
        Request request = new Request();
        String line = buffer.readLine();

        if (line != null) {
            String[] splitFirstLine = line.split(" ");

            request.setMethod(getMethod(splitFirstLine[0]));
            setPathname(request, splitFirstLine[1]);

            line = buffer.readLine();
            while (!line.isEmpty()) {
                request.getHeaderMap().ingest(line);
                line = buffer.readLine();
            }
            if (request.getHeaderMap().getContentLength() > 0) {
                char[] charBuffer = new char[request.getHeaderMap().getContentLength()];
                buffer.read(charBuffer, 0, request.getHeaderMap().getContentLength());
                request.setBody(new String(charBuffer));
            }
        }

        return request;

    }

    private Method getMethod(String methodString) {
        return Method.valueOf(methodString.toUpperCase(Locale.ROOT));
    }

    private void setPathname(Request request, String path){
        Boolean hasParams = path.indexOf("?") != -1;

        if (hasParams) {
            String[] pathParts =  path.split("\\?");
            request.setPathname(pathParts[0]);
            request.setParams(pathParts[1]);
        }
        else
        {
            request.setPathname(path);
            request.setParams(null);
        }
    }
}