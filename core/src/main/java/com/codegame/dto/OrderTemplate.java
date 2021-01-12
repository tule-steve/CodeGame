package com.codegame.dto;

import com.codegame.model.GiftCard;
import com.codegame.model.Order;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class OrderTemplate {
    private String template;

    final private static String TR_START_TAG = "<tr>";

    final private static String TR_END_TAG = "</tr>\n";

    final private static String TD_START_TAG = "<td>";

    final private static String TD_END_TAG = "</td>\n";

    public OrderTemplate() throws Exception {
        this.template = loadTemplate("OrderTemplate.html");
    }

    private String loadTemplate(String customtemplate) throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(customtemplate).getFile());
        String content = "Empty";
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new Exception("Could not read template  = " + customtemplate);
        }
        return content;
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
        sb.append(detail.getCodes());
        sb.append(TD_END_TAG);
    }
}
