package com.bixuebihui.util.html.publish;

public class MyFacesResourceFilter implements IStringFilter {

    @Override
    public String filter(String str) {
        return str.replaceAll("/faces/myFacesExtensionResource/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/(d+)/tree2.HtmlTreeRenderer/javascript", "/res")
                .replaceAll("/faces/myFacesExtensionResource/org.apache.myfaces.renderkit.html.util.MyFacesResourceLoader/(d+)/tree2.HtmlTreeRenderer/images", "/res")
                .replaceAll("org.apache.myfaces.tree.TOGGLE_SPAN:", "");
    }

}
