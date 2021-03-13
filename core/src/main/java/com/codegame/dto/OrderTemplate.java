package com.codegame.dto;

import com.codegame.model.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class OrderTemplate {
    private String template;

    protected final Log logger = LogFactory.getLog(OrderTemplate.class);

    final private static String TR_START_TAG = "<tr>";

    final private static String TR_END_TAG = "</tr>\n";

    final private static String TD_START_TAG = "<td>";

    final private static String TD_END_TAG = "</td>\n";

    public OrderTemplate() throws Exception {
        this.template = loadTemplate("OrderTemplate.html");
    }

    private String loadTemplate(String customtemplate) throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = classLoader.getResourceAsStream(customtemplate);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            throw new Exception("Could not read template  = " + customtemplate);
        }

        return sb.toString();
    }

    public String buildNewLineForOrder(Order order, List<OrderEmailDto> details) {
        StringBuilder sb = new StringBuilder();

        for (OrderEmailDto detail : details) {
            sb.append(TR_START_TAG);
            addGiftCardLine(detail, sb);
            sb.append(TR_END_TAG);
        }

        return template.replace("{{OrderDetail}}", sb.toString());

    }

    private void addGiftCardLine(OrderEmailDto detail, StringBuilder sb) {
        sb.append(TD_START_TAG);
        sb.append(detail.getDescription());
        sb.append(TD_END_TAG);

        sb.append(TD_START_TAG);
        sb.append(detail.getPrice());
        sb.append(TD_END_TAG);

        sb.append(TD_START_TAG);
        sb.append(detail.getCount());
        sb.append(TD_END_TAG);

        sb.append(TD_START_TAG);
        Arrays.stream(detail.getCodes().split(",")).forEach(r -> sb.append(r + "<br/>"));
        sb.append(TD_END_TAG);
    }
}
