package org.apache.poi.xwpf.converter.core.openxmlformats.styles.paragraph;

import org.apache.poi.xwpf.converter.core.styles.XWPFStylesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

public abstract class AbstractIndentationParagraphValueProvider<Value>
    extends AbstractParagraphValueProvider<Value>
{
    @Override
    public Value getValue( CTPPr ppr, XWPFStylesDocument document )
    {
        CTInd ind = getInd( ppr );
        if ( ind == null )
        {
            return null;
        }
        return getValue( ind );
    }

    public CTInd getInd( CTPPr pr )
    {
        if ( pr == null )
        {
            return null;
        }
        return pr.getInd() == null ? null : pr.getInd();
    }

    public abstract Value getValue( CTInd ind );
}
