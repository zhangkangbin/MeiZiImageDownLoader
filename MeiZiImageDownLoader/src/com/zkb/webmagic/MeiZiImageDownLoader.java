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


    private final static String mSavePath="G:\\webmagic\\mzitu3";//�����ͼƬ·��
    private final static String mAddress="Http://www.mzitu.com";//ץȡ�ĵ�ַ
    private final static int mMinSize=40*1024;//��С����
    private final static int mMaxSize=10000*1024;//���
    private static  Spider mSpider;
    private final static int mMaxDownload=5; //�������ͼƬ����  ��������첽���صģ����صĸ������ܴ����������
    private static volatile  int   mTempMaxDownload=0;//��ʱ����
    public void process(Page page) {
  
    	Selectable selectable = page.getHtml().links();  	
    	List<String> url=selectable.regex("^"+mAddress+".*").all();//��ֹ����������վȥ��ƥ�䵱ǰ����վ

    	//ͬʱ��������Ҫ�������ҵ����б�ҳҲ�ӵ������ص�URL��ȥ��
    
        page.addTargetRequests(url);
        //�õ���ǰҳ������img scr �����·��,ʹ�õ���xpath �﷨ƥ��
    	List<String> list = page.getHtml().xpath("//img/@src").all();
    	
   
            for(int i=0;i<list.size();i++){
            
            	try {
            		
            		if(!"".equals(list.get(i))){
            			//����ͼƬ
            		
            		    if(mMaxDownload<mTempMaxDownload){
        	            	
        	            	mSpider.stop();  //��ͣ����
        	            	
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
    			
    			System.out.println("--�ļ��д����ɹ�---");
    		}else{
    			
    			System.out.println("--�ļ��д���ʧ��--ȷ��·������-");
    			return;
    		}
    	}
    	
    	 mSpider=Spider.create(new MeiZiImageDownLoader());
    	 mSpider.addUrl(mAddress);
        //.addPipeline(new JsonFilePipeline("G:\\webmagic"))
    	 mSpider.thread(2);
    	 mSpider.run();//5���߳� �첽
    }
    public static void download(String urlString)  {  
        // ����URL  
        
       	int i=urlString.lastIndexOf("/");
       	String name=urlString.substring(i, urlString.length());
       	 
       	File f=new File(mSavePath+name);
    	
    	if(f.exists()){
    		System.out.println("--�ļ��Ѵ���---"+name);
    		return;
    	}
 
 
        URL url;
		try {
			url = new URL(urlString);
			  
	        // ������  
	        HttpURLConnection  con = (HttpURLConnection) url.openConnection(); 
	        
	        
	        //���ò���  αװ��google�����
	        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	        con.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
	        con.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
	        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
	        con.setRequestProperty("Accept", "__cfduid=d0ad45789bdff1110bee5758b067f1c011461953240");
	    
	        
	        //��������ʱΪ5s  
	        con.setConnectTimeout(5*1000);  
	        
	        
	       
	        
	        
	        if(con.getResponseCode()==200){
	        	 
	            // ������ļ���  
	           File sf=new File(mSavePath);  
	           if(!sf.exists()){  
	               sf.mkdirs();  
	           }  
	           int size=  con.getContentLength();
	        
	           if(mMaxSize>size&&size<mMinSize){
	         
	        	return;   
	           }
	      
	           System.out.println(urlString+"---"+size);
	           
	           
	         
	           // ������  
	           InputStream is = con.getInputStream();
	      
	          
	        
	           // 1K�����ݻ���  
	           byte[] bs = new byte[1024];  
	           // ��ȡ�������ݳ���  
	           int len;        
	           OutputStream os = new FileOutputStream(sf.getPath()+"\\"+name);  
	           
	           
	            // ��ʼ��ȡ  
	            while ((len = is.read(bs)) != -1) {  
	              os.write(bs, 0, len);  
	            }  
	            
	            mTempMaxDownload++;
	            // ��ϣ��ر���������  
	            
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
