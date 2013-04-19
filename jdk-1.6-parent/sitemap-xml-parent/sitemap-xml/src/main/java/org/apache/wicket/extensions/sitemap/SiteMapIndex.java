package org.apache.wicket.extensions.sitemap;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;

public abstract class SiteMapIndex extends AbstractResource implements Observer {


    private static final String PARAM_SITEMAP_OFFSET = "offset";
    private static final String PARAM_SITEMAP_SOURCEINDEX = "sourceindex";

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";
    private static final String FOOTER = "</sitemapindex>";
    private static final int MAX_BYTES_SITEMAP = 10485760; //10 megabyte
    private static final int MAX_ENTRIES_PER_SITEMAP = 50000;
    private static final SimpleDateFormat STRIPPED_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private String domain;

//    public SiteMapIndex(final PageParameters parameters) {
//        super(parameters);
//        if (!parameters.isEmpty()) {
//            final int index = parameters.get(PARAM_SITEMAP_OFFSET).toInt();
//            final int sourceIndex = parameters.get(PARAM_SITEMAP_SOURCEINDEX).toInt();
//
//            setResponsePage(new Sitemap() {
//                @Override
//                protected SiteMapFeed getFeed() {
//                    final SiteMapFeed feed = new SiteMapFeed(new IOffsetSiteMapEntryIterable.SiteMapIterable() {
//                        public IOffsetSiteMapEntryIterable.SiteMapIterator iterator() {
//                            return getDataSources()[sourceIndex].getIterator(index);
//                        }
//                    });
//                    feed.addObserver(SiteMapIndex.this);
//                    return feed;
//                }
//            });
//        }
//    }

    public String getDomain() {
        if (domain == null) {
            final Request rawRequest = RequestCycle.get().getRequest();
            if (!(rawRequest instanceof WebRequest)) {
                throw new WicketRuntimeException("sitemap.xml generation is only possible for http requests");
            }
            WebRequest wr = (WebRequest) rawRequest;
            domain = "http://" + ((HttpServletRequest)wr.getContainerRequest()).getHeader("host");
        }
        return domain;
    }

    public void update(Observable o, Object arg) {
        //todo feedback loop to adjust block sizes
        if (o instanceof SiteMapFeed) {
            final SiteMapFeed siteMapFeed = (SiteMapFeed) o;
            if ((siteMapFeed.getEntriesWritten() > MAX_ENTRIES_PER_SITEMAP) ||
                    (siteMapFeed.getBytesWritten() > MAX_BYTES_SITEMAP)) {
                throw new IllegalStateException("please adjust block sizes for this sitemap.");
            }
        }
    }
    
//	@Override
//	public MarkupType getMarkupType(){
//		return new MarkupType("xml", "text/xml");
//	}

//    public abstract IRequestTargetUrlCodingStrategy mountedAt();

      
//    @Override
//    protected void onRender(final MarkupStream markupStream) {
//        PrintWriter w = new PrintWriter(getResponse().getOutputStream());
//        try {
//            w.write(HEADER);
//            int sourceNumber = 0;
//            for (IOffsetSiteMapEntryIterable dataBlock : getDataSources()) {
//                for (int i = 0; i < dataBlock.getUpperLimitNumblocks(); i++) {
//                    w.append("<sitemap>\n<loc>");
//                    final PageParameters params = new PageParameters();
//                    params.put(PARAM_SITEMAP_SOURCEINDEX, String.valueOf(sourceNumber));
//                    params.put(PARAM_SITEMAP_OFFSET, String.valueOf(i * dataBlock.getElementsPerSiteMap()));
//                    final String url = getDomain() + "/" + String.valueOf(mountedAt().encode(new BookmarkablePageRequestTarget(getClass(), params)));
//                    w.append(StringEscapeUtils.escapeXml(url));
//                    w.append("</loc>\n<lastmod>");
//                    w.append(STRIPPED_DAY_FORMAT.format(dataBlock.changedDate()));
//                    w.append("</lastmod>\n</sitemap>\n");
//                }
//                sourceNumber++;
//            }
//            w.write(FOOTER);
//            w.flush();
//        } finally {
//            w.close();
//        }
//    }
    
    @Override
    protected ResourceResponse newResourceResponse(Attributes attributes) {
    	ResourceResponse response = new ResourceResponse();
    	response.setWriteCallback(new WriteCallback() {
			@Override
			public void writeData(final Attributes attributes) {
			      PrintWriter w = new PrintWriter(attributes.getResponse().getOutputStream());
			      try {
			          w.write(HEADER);
			          int sourceNumber = 0;
			          for (IOffsetSiteMapEntryIterable dataBlock : getDataSources()) {
			              for (int i = 0; i < dataBlock.getUpperLimitNumblocks(); i++) {
			                  w.append("<sitemap>\n<loc>");
			                  final PageParameters params = new PageParameters();
//			                  params.put(PARAM_SITEMAP_SOURCEINDEX, String.valueOf(sourceNumber));
//			                  params.put(PARAM_SITEMAP_OFFSET, String.valueOf(i * dataBlock.getElementsPerSiteMap()));
//			                  final String url = getDomain() + "/" + String.valueOf(mountedAt().encode(new BookmarkablePageRequestTarget(getClass(), params)));
			                  final String url = getDomain() + "/sitemap.xml";
			                  w.append(StringEscapeUtils.escapeXml(url));
			                  w.append("</loc>\n<lastmod>");
			                  w.append(STRIPPED_DAY_FORMAT.format(dataBlock.changedDate()));
			                  w.append("</lastmod>\n</sitemap>\n");
			              }
			              sourceNumber++;
			          }
			          w.write(FOOTER);
			          w.flush();
			      } finally {
			          w.close();
			      }

			}
		});
    	response.setFileName("sitemap.xml");
    	return response;
    };
    

    public abstract IOffsetSiteMapEntryIterable[] getDataSources();
}
