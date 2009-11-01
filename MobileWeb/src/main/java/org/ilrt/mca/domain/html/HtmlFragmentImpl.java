package org.ilrt.mca.domain.html;

import org.ilrt.mca.domain.BaseItem;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 */
public class HtmlFragmentImpl extends BaseItem implements HtmlFragment {

    public HtmlFragmentImpl() {
    }

    @Override
    public String getHtmlFragment() {
        return htmlFragment;
    }

    public void setHtmlFragment(String htmlFragment) {
        this.htmlFragment = htmlFragment;
    }

    private String htmlFragment;
}
