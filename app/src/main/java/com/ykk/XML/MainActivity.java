package com.ykk.XML;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= (Button) findViewById(R.id.xml_id);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       HttpURLConnectionMethod();
        //HttpClinentMethod();

    }

    //SAX解析方法
    private void pareXMLWithSAX(String s)
    {
        SAXParserFactory factory=SAXParserFactory.newInstance();
        try {
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            ContentHandler handler=new ContentHandler();
            //将ContentHandler的实例设置到XMLReader中.
            xmlReader.setContentHandler(handler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(s)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Pull解析XML数据
    private void parseXMLWithPull(String data)
    {
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(data));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while(eventType!=XmlPullParser.END_DOCUMENT)
            {
                String nodeName=xmlPullParser.getName();
                switch (eventType)
                {
                    //开始解析某个节点
                    case XmlPullParser.START_TAG:
                    {
                        if("id".equals(nodeName))
                        {
                            id=xmlPullParser.nextText();
                        }
                        else if("name".equals(nodeName))
                        {
                            name=xmlPullParser.nextText();
                        }
                        else if ("version".equals(nodeName))
                        {
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    //完成解析某个节点
                    case XmlPullParser.END_TAG:
                    {
                        if("app".equals(nodeName))
                        {
                            Log.d("MainActivity","Pull "+"id  "+id);
                            Log.d("MainActivity","Pull "+"name is "+name);
                            Log.d("MainActivity","Pull "+"version is "+version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                //调用next方法获取下一个解析事件。
                eventType=xmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //使用HttpClient方法方法网络
    public void HttpClinentMethod()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient=new DefaultHttpClient();
                HttpGet httpGet=new HttpGet("http://192.168.1.112/data.xml");
                try {
                    HttpResponse httpResponse=httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK)
                    {
                        HttpEntity entity=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity,"utf-8");
                        parseXMLWithPull(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //使用HttpURLConnection方法访问网络
    private void HttpURLConnectionMethod()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL("http://192.168.1.112/data.xml");
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder=new StringBuilder();
                    String data=null;
                    while((data=reader.readLine())!=null)
                    {
                        builder.append(data);
                    }
                    //使用Pull方法解析
                    //parseXMLWithPull(builder.toString());
                    //使用SAX方法解析
                    pareXMLWithSAX(builder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
