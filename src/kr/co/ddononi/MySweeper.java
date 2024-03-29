package kr.co.ddononi;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


public class MySweeper {
	private static void showMenu(){
		System.out.println("-------------------------------");
		System.out.println("              TuTuSweeper      ");
		System.out.println("-------------------------------");
		System.out.println("     게시판주소로 스윕하기 1번         ");
		System.out.println("     아이디값으로 스윕하기 2번         ");
		System.out.println("-------------------------------");
		System.out.println("   made by ddononi (ver 1.00)  ");
		System.out.println("-------------------------------");
		System.out.print  ("     메뉴를 입력하세요 :         ");
//		String s = "<script>document.domain='fewoo.net';</script>		<script language=\"JavaScript\">\n"
//		+ "window.opener.insertImageSrc(\"http://club.fewoo.net/dramaworld/data/__132808007733647.jpg\");\n;"
//		+ "window.close();</script>";
	}

	/**
	 * 아이디값을 이용하여 Content를 스윕한다.
	 */
	private void searchFromId(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in)); // 키보드입력스트림
		int count = 0;
		String line;
		try {
			while(true){
				System.out.print("아이디를 입력하세요 : ");
				line = "";
					line = reader.readLine();
				if(line.equalsIgnoreCase("bye")){ //	종료
					break;
				}else if(line.length() < 5){
					continue;
				}
			// 시작 시간 체크
				long startTime = System.currentTimeMillis();

				TuTuSweeper sp = new TuTuSweeper();
				sp.getStartContent(line);

				long endTime = System.currentTimeMillis();
				endTime = (endTime - startTime);
				System.out.println("자료 수집 시간 : " + endTime + "Millis" );
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {}
		}

		System.out.println("총 수집 자료 : " + count + "개");
		System.out.println("bye~ bye~");
	}

	private void searchFromUrl(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in)); // 키보드입력스트림
		int count = 0;
		String url;
		try {
			while(true){
				System.out.print("주소를 입력하세요 : ");
				url = "";
				url = reader.readLine();
				if(url.equalsIgnoreCase("bye")){ //	종료
					break;
				}else if(url.length() < 10){
					continue;
				}
				// 시작 시간 체크
				long startTime = System.currentTimeMillis();

				TuTuSweeper sp = new TuTuSweeper();
				sp.getStartMultiContents(url);

				long endTime = System.currentTimeMillis();
				endTime = (endTime - startTime) / 1000;
				System.out.println("게시판 자료 수집 시간 : " + endTime + "초" );
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {}
		}

		System.out.println("총 수집 자료 : " + count + "개");
		System.out.println("bye~ bye~");
	}

	//private String dir;
	public static void main(final String[] args){
		/*
		if(args.length < 1) {
			System.err.println("Usage: java TutuSweeper [tutu-id Numbers]... ");
			System.exit(1);
		}
		*/

		showMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in)); // 키보드입력스트림
		String selNum;
		try {
			while(true){
				reader.ready();
				selNum = reader.readLine();
				if(selNum.equals("1")){
					System.out.println("\n 게시판 주소으로 검색합니다!\n");
					MySweeper tutu = new MySweeper();
					tutu.searchFromUrl();
					break;
				}else if(selNum.equals("2")){
					System.out.println("\n아이디로 검색합니다!\n");
					MySweeper tutu = new MySweeper();
					tutu.searchFromId();
					break;
				}else{
					System.out.println(selNum);
					System.out.println("\n정확한 메뉴를 선택하세요!!\n");
					showMenu();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println("정확한 메뉴를 선택하세요");
		}finally{
			try {
				reader.close();
			} catch (IOException e) {}
		}

	}
}


class TuTuSweeper{
	private String dir = null;
	private String id = null;
	public final static String TUTU_URL =
			"http://www.tutudisk.com//main/popup/bbs_info_0.php?idx=";	// 투투웹하드 주소
	public final static String SAVE_FILE_NAME = "content.txt";			// 저장파일명
	public final static String IMAGE_UPLOAD_URL =
			"http://onehard.fewoo.net/fseditor/image_control.php";		// 이미지 업로드 url
	public final static String RESPONSE_UPLOAD_URL =					// 이미지 업로드 응답 url
			"http://club.fewoo.net/dramaworld/data";
	public final static int MAX_UPLOAD_SIZE = 737280;					// 최대 파일 크기
	public final static String NO_PARTNER =
			"http://webimage.tutudisk.com/icon/icon_off.gif";			// 노제휴 나무 이미지

	public TuTuSweeper(){
	}

	/**
	 * 년 월 일 형식으로 디렉토리를 만들어준다.
	 * 디렉토리를 만들어준다.
	 */
	private void makeDir() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder("D:\\tmp");
		sb.append(File.separator);
		/*
		sb.append(year);
		sb.append(File.separator);
		sb.append(month +"월");
		sb.append(File.separator);
		sb.append(day + "일");
		sb.append(File.separator);
		*/
		sb.append(getTitle());	// 제목을 추출후 폴더명으로
		sb.append(File.separator);
		File file = new File(sb.toString());
		if( file.mkdirs() ){
			System.out.println(file.toString() + " 폴더를 만들었습니다.");
		}
		// 저장할 폴더
		dir = file.getAbsolutePath();
	}

	/**
	 * content 내용을 파일로 저장한다.
	 * @param source
	 * 	content 내용
	 * @return
	 * 	저장여부
	 */
	private boolean doSaveFile(final String source) {
		File sourcefile = new File(this.dir, SAVE_FILE_NAME);
		BufferedWriter bw = null;
		boolean flag = true;
		try {
			bw = new BufferedWriter(new FileWriter(sourcefile));
			bw.write(source);
			bw.flush();
			System.out.println("소스파일을 저장했습니다.");
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {}
		}

		return flag;
	}

	/**
	 * 주어진 url로 해당 url소스를 가져온다.
	 * @return url source
	 */
	protected String downloadSource(){
		URL url = null;
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = null;
		try {
			url = new URL(TUTU_URL+ id);
			 URLConnection conn = url.openConnection();
			 is = conn.getInputStream();
			 br = new BufferedReader(new InputStreamReader(is));
			 while((line = br.readLine()) != null) {
				sb.append(line).append("\r\n");
			 }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}

		return sb.toString();
	}

	/**
	 * content에서 추출된 이미지 url를 로컬에 이미지파일로 저장한다.
	 * @param source
	 *		image url
	 * @return
	 * 		저장된 File 객체
	 */
	private File saveToImageFile(final String source){
		File filename = new File(source);
		FileOutputStream imageFile = null;
		BufferedInputStream bis = null;
		File file = new File(this.dir, filename.getName());
		try {
			imageFile = new FileOutputStream(file);
			URL url = new URL(source);
			bis = new BufferedInputStream(url.openStream());
			int size;
			while( (size = bis.read()) > -1){
				imageFile.write(size);
			}
			imageFile.flush();
			if(file.length() <=0){	// 파일크기가 0일경우
				System.out.println("이미지 저장 실패!");
				return null;
			}
			System.out.println(filename.getName() + " 이미지 저장 완료");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			try {
				imageFile.close();
				bis.close();
			} catch (IOException e) {}
		}
		return file;
	}

	/**
	 * 제리코 라이브러리를 이용하여 content url의 html을
	 * 파싱한후  제목을 얻어온다.	html으로 데이터를
	 * 얻어오기 때문에 추후 변경될 소지가 있다.
	 * @return
	 * 	저장할 제목
	 */
	private String getTitle(){
		String title = null;
		try {
			Source source = new Source(new URL(TUTU_URL+ this.id));
			source.fullSequentialParse();
			List<Element> rootList = source.getAllElements(HTMLElementName.TD);
			String width;
			for(Element elem : rootList){
				width = elem.getAttributeValue("width");
				if(width == null) {
					continue;
				}else if(width.contains("605")){
					// 타이틀명을 가져온후 좌우 공백제거
					title = elem.getAttributeValue("title").toString().trim();
					System.out.println("제목 : " + title);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return title/*.replace(".", "")*/.replace("/", "//");
	}

	public void getStartContent(final String id){
		this.id = id;
		makeDir();	// 디렉토리를 만든후
		try {
			Source source = new Source(new URL(TUTU_URL + id));
			source.fullSequentialParse();
			List<Element> tdList = source.getAllElements(HTMLElementName.TD);
			for(Element elem : tdList){
				String style = elem.getAttributeValue("style");
				if(style == null) {
					continue;
				}else if(style.contains("word-break:break-all;font-size:9pt")
						&& elem.getAttributeValue("valign").contains("top")
						&& elem.getAttributeValue("align").contains("center")){	// 업로드 부분 추출
					String contents = elem.getContent().toString().trim();
					// 이미지를 제거한 내용만 추출
					contents = contents.replaceAll("<IMG ([^>]+)>", "");
					List<Element> imageList = elem.getAllElements(HTMLElementName.IMG);	// 이미지만 추출
					String uploadTag = "";
					for(Element subElem : imageList){
						String src = subElem.getAttributeValue("src");
						if(src == null) {
							continue;
						}
						// 업로드 이미지만 추출
						System.out.println(src + "를 찾았습니다.");
						File file = saveToImageFile(src);
						if(file != null){	// 파일이 정상적으로 저장 되었을 경우만
							System.out.println("이미지파일 크기 : " + file.length() +"bytes");
							// 파일사이즈 줄이기       750lkb 이하
							while(file.length() > MAX_UPLOAD_SIZE ){
								BufferedImage originalImage = ImageIO.read(file);
								int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

								BufferedImage resizeImageJpg = resizeImage(originalImage, type);
								ImageIO.write(resizeImageJpg, "jpg", file);
							}

							uploadTag += uploadImage(file);
						}
					}
					doSaveFile(uploadTag + contents);	// 내용 저장
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getStartMultiContents(final String url){
		ArrayList<Content> contentsList = new ArrayList<Content>();
		try {
			Source source = new Source(new URL(url));
			source.fullSequentialParse();
			List<Element> divList = source.getAllElements(HTMLElementName.DIV);
			for(Element elem : divList){
				String style = elem.getAttributeValue("style");
				if(style == null) {
					continue;
				}else if(style.contains("visibility:hidden;z-index:9999;position:absolute;width=100%;background-color:white;top:6px")){	// 업로드 부분 추출
					List<Element> imageList = elem.getAllElements(HTMLElementName.IMG);	// 이미지만 추출
					for(Element subElem : imageList){
						String src = subElem.getAttributeValue("src");
						if(src == null) {
							continue;
						}else if(src.contains(NO_PARTNER)){
							// 제휴 컨탠츠가 아닌경우만
							Content c = new Content();
							String id = elem.getAttributeValue("id").replace("full_title_", "");
							getStartContent(id);
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 갈무리된 이미지 파일을 원하드 서버에 업로드 한다.
	 * @param file
	 *		저장된 이미지 파일
	 * @return
	 * 		전송 성공시 업로드된 이미지 태그 ex:) <img src='some_image.jpg'>
	 */
	private String uploadImage(final File file){
		String tag = "";	// 저장될 태그
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody encFile = new FileBody(file);	// 업로드할 이미지 파일 저장

        /*
         *
         *  FORM 내용
         *  <input type="hidden" name="pg_mode" value="insert">
         *	<input type="hidden" name="img_width">
         *	<input type="hidden" name="img_height">
         *	<input type="hidden" name="limit_width" value="">
         *	<input type="hidden" name="limit_height" value="">
         *	<input type="hidden" name="get_mode" value="0">
         *	<input type="hidden" name="save_path" value="dramaworld/data">
         *  <input type="hidden" name="exten_clubid" value="dramaworld">
         *  <input type="file" name="targetfile" onchange="ShowImg(this, 'preView') />
         */

        // input 내용 넣기
        entity.addPart("targetfile", encFile);
        try {
            entity.addPart("pg_mode", new StringBody("insert"));
            entity.addPart("save_path", new StringBody("dramaworld/data"));
            entity.addPart("exten_clubid", new StringBody("dramaworld"));
        } catch (UnsupportedEncodingException e) {
        	System.out.println("error");
        }

        // post 방식으로 전송
        HttpPost request = new HttpPost(IMAGE_UPLOAD_URL);
        request.setEntity(entity);
        HttpClient client = new DefaultHttpClient();

        try {
            BasicResponseHandler responseHandler = new BasicResponseHandler();
            String responseBody = client.execute(request, responseHandler);

            // 정상적으로 등록시 업로드된 이미지 url을 찾아서 img tag로 만들어 준후
            // content 파일에 넣어주어 나중에 content파일 내용을 복사하여 붙여넣기로
            // 할수 있도록 만든다.
            if (responseBody != null && responseBody.length() > 0
            		&& responseBody.contains(RESPONSE_UPLOAD_URL)) {
            	try{
		       		String[] arr = responseBody.split("//");
		    		String src = arr[1].split("(;)")[0];
		    	    tag = "<img src='http://" + src.replace(")", "").replace("\"", "")
		    				 +"' border='0' onLoad='javascript:"
		    				 +"if(this.width>600) this.width=600;'><br>";
		    	    System.out.println(tag);
            	}catch(IndexOutOfBoundsException ibe){
            		 System.out.println(responseBody);
            	}
//            	  추후 패턴형식으로 변경 될수 있음.
//               Pattern pattern;
//               Matcher matcher;
//               pattern = Pattern.compile("http:+;");
//               matcher = pattern.matcher(responseBody);
//               if(matcher.matches()) {
//               	 System.out.println("find"); // matcher에서 매칭된 것 전체를 반환
//                   System.out.println(matcher.group()); // matcher에서 매칭된 것 전체를 반환
//               }
            }else{
            	 System.out.println("**************저장 실패~~~~!!");
            	 System.out.println(responseBody);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tag;
	}

	private static BufferedImage resizeImage(final BufferedImage originalImage, final int type){
		int width = (int) (originalImage.getWidth() * .8);
		int height = (int) (originalImage.getHeight() * .8);
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}

	public class Content{
		String id;
		boolean partnership;
		int size;
	}
}
