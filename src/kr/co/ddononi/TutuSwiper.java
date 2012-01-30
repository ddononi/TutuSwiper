package kr.co.ddononi;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class TutuSwiper {
	//private String dir;
	public static void main(final String[] args){
		/*
		if(args.length < 1) {
			System.err.println("Usage: java TutuSwiper [tutu-id Numbers]... ");
			System.exit(1);
		}
		*/

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in)); // 키보드입력스트림
		int count = 0;
		String line;
		while(true){
			System.out.print("투투 아이디를 입력하세요 : ");
			line = "";
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			if(line.length() < 5){
				continue;
			}else if(line.equalsIgnoreCase("bye")){			//	종료
				break;
			}

			// 시작 시간 체크
			long startTime = System.currentTimeMillis();

			Swiper sp = new Swiper(line);
			sp.getStartContents();

			long endTime = System.currentTimeMillis();
			endTime = (endTime - startTime);
			System.out.println("자료 수집 시간 : " + endTime + "Millis" );
			count++;
		}

		System.out.println("총 수집 자료 : " + count + "개");
		System.out.println("bye~ bye~");
	}
}


class Swiper{
	private String dir = null;
	public final static String TUTU_URL =
			"http://www.tutudisk.com//main/popup/bbs_info_0.php?idx=";
	public final static String SAVE_FILE_NAME = "info.html";
	private String id;

	public Swiper(){
		//12107151
		System.out.println("아이디가 없습니다.");
	}

	public Swiper(final String id){
		// 폴더 생성
		this.id = id;
		File file = makeDir();
		if( file.mkdirs() ){
			System.out.println(file.toString() + " 폴더를 만들었습니다.");
		}

		// 저장할 폴더
		dir = file.getAbsolutePath();

		// 소스 저장처리
		//String source = downloadSource();
	}

	/**
	 * 년 월 일 형식으로 디렉토리를 만들어준다.
	 * 디렉토리를 만들어준다.
	 * @return 파일 디스크립터
	 */
	private File makeDir() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		StringBuilder sb = new StringBuilder("C:\\onehard");
		sb.append(File.separator);
		sb.append(year);
		sb.append(File.separator);
		sb.append(month);
		sb.append(File.separator);
		sb.append(day + "월");
		sb.append(File.separator);
		sb.append(getTitle());	// 제목을 추출후 폴더명으로
		sb.append(File.separator);
		File file = new File(sb.toString());

		return file;
	}

	protected boolean doSaveFile(final String source) {
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
			url = new URL(TUTU_URL+ this.id);
			 URLConnection conn = url.openConnection();
			 is = conn.getInputStream();
			 br = new BufferedReader(new InputStreamReader(is));
				while((line = br.readLine()) != null) {
					sb.append(line);
					sb.append("\r\n");
				}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
				is.close();
			} catch (IOException e) {}
		}

		return sb.toString();
	}

	private void saveToImageFile(final String source){
		File filename = new File(source);
		//File file = new File("C:\\onehard", source);
		FileOutputStream imageFile = null;
		BufferedInputStream bis = null;
		try {
			imageFile = new FileOutputStream(new File(this.dir, filename.getName()));
			URL url = new URL(source);
			bis = new BufferedInputStream(url.openStream());
			int size;
			while( (size = bis.read()) > -1){
				imageFile.write(size);
			}

			imageFile.flush();
			System.out.println(filename.getName() + " 이미지 저장 완료");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				imageFile.close();
				bis.close();
			} catch (IOException e) {}
		}

	}

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

		return title.replace(".", "");
	}

	public void getStartContents(){
		try {
			Source source = new Source(new URL(TUTU_URL+ this.id));
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
					doSaveFile(contents);	// 내용 저장
					List<Element> imageList = elem.getAllElements(HTMLElementName.IMG);	// 이미지만 추출
					for(Element subElem : imageList){
						String src = subElem.getAttributeValue("src");
						if(src == null) {
							continue;
						}
						// 업로드 이미지만 추출
						System.out.println(src + "를 찾았습니다.");
						saveToImageFile(src);
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
}
