package com.zkb.webmagic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;



public class MeiZiImageDownLoader implements PageProcessor{


    private final static String mSavePath="G:\\webmagic\\mzitu3";//保存的图片路径
    private final static String mAddress="Http://www.mzitu.com";//抓取的地址
    private final static int mMinSize=40*1024;//最小接受
    private final static int mMaxSize=10000*1024;//最大
    private static  Spider mSpider;
    private final static int mMaxDownload=5; //最大下载图片个数  ，如果是异步下载的，下载的个数可能大于这个数。
    private static volatile  int   mTempMaxDownload=0;//临时变量
    public void process(Page page) {
  
    	Selectable selectable = page.getHtml().links();  	
    	List<String> url=selectable.regex("^"+mAddress+".*").all();//防止爬到其他网站去。匹配当前的网站

    	//同时，我们需要把所有找到的列表页也加到待下载的URL中去：
    
        page.addTargetRequests(url);
        //得到当前页面所有img scr 里面的路径
    	List<String> list = page.getHtml().xpath("//img/@src").all();
    	
   
            for(int i=0;i<list.size();i++){
            
            	try {
            		
            		if(!"".equals(list.get(i))){
            			//下载图片
            		
            		    if(mMaxDownload<mTempMaxDownload){
        	            	
        	            	mSpider.stop();  //暂停爬虫
        	            	
        	            }else{
        	            	download(list.get(i));
        	            }
            			
            			}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //page.putField("repo", list.get(i) );
            }
                
 
    }

    @Override
    public Site getSite() {
    	
         Site site = Site.me();
         site.setRetryTimes(3);
         site.setSleepTime(0);
         site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36"
         		+ " (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
       
    	
        return site;
    }

 
    public static void main(String[] args) {
    
    	File file=new File(mSavePath);
    	if(!file.exists()){
    		if(file.mkdirs()){
    			
    			System.out.println("--文件夹创建成功---");
    		}else{
    			
    			System.out.println("--文件夹创建失败--确定路径可用-");
    			return;
    		}
    	}
    	
    	 mSpider=Spider.create(new MeiZiImageDownLoader());
    	 mSpider.addUrl(mAddress);
        //.addPipeline(new JsonFilePipeline("G:\\webmagic"))
    	 mSpider.thread(2);
    	 mSpider.run();//5条线程 异步
    }
    public static void download(String urlString)  {  
        // 构造URL  
        
       	int i=urlString.lastIndexOf("/");
       	String name=urlString.substring(i, urlString.length());
       	 
       	File f=new File(mSavePath+name);
    	
    	if(f.exists()){
    		System.out.println("--文件已存在---"+name);
    		return;
    	}
 
 
        URL url;
		try {
			url = new URL(urlString);
			  
	        // 打开连接  
	        HttpURLConnection  con = (HttpURLConnection) url.openConnection(); 
	        
	        
	        //设置参数  伪装成google浏览器
	        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	        con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
	        con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
	        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
	        con.setRequestProperty("Accept", "__cfduid=d0ad45789bdff1110bee5758b067f1c011461953240");
	    
	        
	        //设置请求超时为5s  
	        con.setConnectTimeout(5*1000);  
	        
	        
	       
	        
	        
	        if(con.getResponseCode()==200){
	        	 
	            // 输出的文件流  
	           File sf=new File(mSavePath);  
	           if(!sf.exists()){  
	               sf.mkdirs();  
	           }  
	           int size=  con.getContentLength();
	        
	           if(mMaxSize>size&&size<mMinSize){
	         
	        	return;   
	           }
	      
	           System.out.println(urlString+"---"+size);
	           
	           
	         
	           // 输入流  
	           InputStream is = con.getInputStream();
	      
	          
	        
	           // 1K的数据缓冲  
	           byte[] bs = new byte[1024];  
	           // 读取到的数据长度  
	           int len;        
	           OutputStream os = new FileOutputStream(sf.getPath()+"\\"+name);  
	           
	           
	            // 开始读取  
	            while ((len = is.read(bs)) != -1) {  
	              os.write(bs, 0, len);  
	            }  
	            
	            mTempMaxDownload++;
	            // 完毕，关闭所有链接  
	            
	            os.close();  
	            is.close(); 
	        
	        	
	        }
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
       
      
        
       
     
    }   
    
}
