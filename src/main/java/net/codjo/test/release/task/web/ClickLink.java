package net.codjo.test.release.task.web;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
/**
 *
 */
public class ClickLink implements WebStep {

    private String text;
    private String id;


    public void proceed(WebContext context) throws IOException {
        if ((text != null) && (id != null)) {
            throw new WebException("Les champs 'text' et 'id' ne doivent pas �tre utilis�s en m�me temps");
        }

        try {
            HtmlAnchor anchor = findAnchor(context);
            context.setPage((HtmlPage)anchor.click());
        }
        catch (FailingHttpStatusCodeException e) {
            throw new WebException("Erreur lors du click sur le lien '" + text +
                                   "' : " + e.getMessage());
        }
    }


    private HtmlAnchor findAnchor(WebContext context) {
        HtmlPage page = context.getHtmlPage();
        if (text != null) {
            text = context.replaceProperties(text);
            try {
                return page.getAnchorByText(text);
            }
            catch (ElementNotFoundException e) {
                throw new WebException("Aucun lien trouv� avec le texte: " + text);
            }
        }
        if (id != null) {
            try {
                HtmlElement element = page.getHtmlElementById(id);
                if (!(element instanceof HtmlAnchor)) {
                    throw new WebException("L'�l�ment '" + id + "' n'est pas un lien <a ...>");
                }
                return (HtmlAnchor)element;
            }
            catch (ElementNotFoundException e) {
                throw new WebException("Aucun lien trouv� avec l'identifiant: " + id);
            }
        }
        throw new WebException("Le champ 'text' ou 'id' doit �tre sp�cifi�");
    }


    public void setText(String text) {
        this.text = text;
    }


    public void setId(String id) {
        this.id = id;
    }
}
