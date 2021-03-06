package cc.mrbird.web.common.thymeleaf.dict;

import cc.mrbird.system.domain.Dict;
import cc.mrbird.system.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;


@Slf4j
public class DictSelectProcessor extends AbstractElementTagProcessor {

    private static final String TAG_NAME = "select";// 标签名
    private static final int PRECEDENCE = 300;
    private static final String FIELD_NAME = "fieldName";// 字典编码
    private static final String KEYY = "keyy";// 字典编码   已选中
    private static final String CLS = "class";//样式
    private static final String NAME = "name";//form name
    private static final String ID = "id";//form id

    public DictSelectProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);

    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
                             IElementTagStructureHandler structureHandler) {
        String fieldName = tag.getAttribute(FIELD_NAME).getValue();
        IAttribute valueAttr = tag.getAttribute(KEYY);
        IAttribute clsAttr = tag.getAttribute(CLS);
        IAttribute nameAttr = tag.getAttribute(NAME);
        IAttribute idAttr = tag.getAttribute(ID);
        final IEngineConfiguration configuration = context.getConfiguration();
        final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        String realvalue = "";
        if (valueAttr != null) {
            final IStandardExpression valueexpression = parser.parseExpression(context, valueAttr.getValue());
            realvalue = String.valueOf(valueexpression.execute(context));

            if (log.isDebugEnabled()) {
                log.debug("dict== fieldName:{}, keyy:{}, realvalue:{}", fieldName, valueAttr.getValue(), realvalue);
            }
        }
        ApplicationContext appCtx = SpringContextUtils.getApplicationContext(context);
        DictService dictService = appCtx.getBean(DictService.class);
        List<Dict> dicts = dictService.findDictByFieldName(fieldName);
        StringBuffer options = new StringBuffer();
        String selected = "";
        for (Dict dict : dicts) {
            if (valueAttr != null && realvalue.equals(dict.getKeyy())) {
                selected = " selected=selected ";
            } else {
                selected = "";
            }
            options.append("<option value=\"" + dict.getKeyy() + "\" " + selected + ">" + dict.getValuee() + "</option>");
        }
        StringBuffer select = new StringBuffer("<select");
        if (clsAttr != null) {
            select.append(" class=\"" + clsAttr.getValue() + "\"");
        }
        if (nameAttr != null) {
            select.append(" name=\"" + nameAttr.getValue() + "\"");
        }
        if (idAttr != null) {
            select.append("  id=\"" + idAttr.getValue() + "\"");
        }
        select.append(">");
        select.append(options);
        select.append("<select>");

        structureHandler.replaceWith(select.toString(), false);
    }

}
