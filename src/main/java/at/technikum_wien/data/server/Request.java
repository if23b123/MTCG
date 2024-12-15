package at.technikum_wien.data.server;

import at.technikum_wien.data.http.Method;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.Headers;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class Request {
    private Method method;
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private HeaderMap headermap = new HeaderMap();
    private String body;

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                    part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }
    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
        Boolean hasParams = urlContent.indexOf("?") != -1;

        if (hasParams) {
            String[] pathParts =  urlContent.split("\\?");
            this.setPathname(pathParts[0]);
            this.setParams(pathParts[1]);
        }
        else
        {
            this.setPathname(urlContent);
            this.setParams(null);
        }
    }

    public String getServiceRoute(){
        if (this.pathParts == null ||
                this.pathParts.isEmpty()) {
            return null;
        }
        String decodedPath = URLDecoder.decode(this.pathParts.get(0), StandardCharsets.UTF_8);

        String[] pathSegments = decodedPath.split(" ");

        return "/" + pathSegments[0] ;
    }

    public HeaderMap getHeaderMap() {
        return headermap;
    }
}
