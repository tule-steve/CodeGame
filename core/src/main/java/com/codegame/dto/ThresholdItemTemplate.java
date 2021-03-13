package com.codegame.dto;

import com.codegame.model.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ThresholdItemTemplate {
    private String template;

    protected final Log logger = LogFactory.getLog(ThresholdItemTemplate.class);

    final private static String TR_START_TAG = "<tr>";

    final private static String TR_END_TAG = "</tr>\n";

    final private static String TD_START_TAG = "<td>";

    final private static String TD_END_TAG = "</td>\n";

    public ThresholdItemTemplate() throws Exception {
        this.template = loadTemplate("NotifyThresholdItemTemplate.html");
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

    public String buildTemplate(List<ItemDto> details) {
        StringBuilder sb = new StringBuilder();

        for (ItemDto detail : details) {
            sb.append(TR_START_TAG);
            addGiftCardLine(detail, sb);
            sb.append(TR_END_TAG);
        }

        return template.replace("{{ThresholdItemDetail}}", sb.toString());

    }

    private void addGiftCardLine(ItemDto detail, StringBuilder sb) {
        sb.append(TD_START_TAG);
        sb.append(detail.getItemId());
        sb.append(TD_END_TAG);

        sb.append(TD_START_TAG);
        sb.append(detail.getDescription());
        sb.append(TD_END_TAG);

        sb.append(TD_START_TAG);
        sb.append(detail.getCount());
        sb.append(TD_END_TAG);
    }
}
