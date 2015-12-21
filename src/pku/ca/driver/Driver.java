package pku.ca.driver;
import java.io.*;
import java.util.ArrayList;

import javax.annotation.processing.FilerException;

import pku.ca.cache.*;
import pku.ca.addrparser.*;
import pku.ca.memory.*;
import pku.ca.replacement.*;
import pku.ca.util.*;

public class Driver {
	/*	地址
		地址解析
		在cache中
			行选
			tag匹配：Miss/Hit
				Miss：
					从内存中调一行，换出cache，换进cache
			读写数据
				读：直接读
				写：回写
	*/
	//cache manager
	public CacheManager cachemanage;
	//构建指令和输出
	public ArrayList<Request> arrayRequest;
	public ArrayList<Response> arrayResponse;
	
	//静态输出指令
	public String instrs = "";
	public ArrayList<String> AL_Str;
	public int tick = 0;
	public int boundtick = 0;
	
	public void start(String filePath) throws IOException {
		cachemanage = new CacheManager();
		arrayRequest = new ArrayList<Request>();
		arrayResponse = new ArrayList<Response>();
		instrs = "";
		AL_Str = new ArrayList<String>();
		tick = 0;
		boundtick = 0;
		
		cachemanage.initMem();
		InputStream is = new FileInputStream(filePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		//读文件
		int num = 0;
		line = reader.readLine();
		while(line != null) {
			//保存指令
			AL_Str.add(convertInstr(line));
			instrs = instrs+ String.valueOf(num+1) + ". " + convertInstr(line);
			num ++;
			//分解指令
			String splitoNum[] = line.split(" ");
			Request r = new Request(Integer.parseInt(splitoNum[0]), Integer.parseInt(splitoNum[1],16),
					Integer.parseInt(splitoNum[2],16), Integer.parseInt(splitoNum[3]), Integer.parseInt(splitoNum[4]));
			arrayRequest.add(r);
			line = reader.readLine();
		}
		reader.close();
		
		for(int i=0; i<arrayRequest.size(); i++) {
			Response r = new Response();
			arrayResponse.add(r);
		}
		boundtick = arrayRequest.size()+2;
	}
	public void oneTick() {
		if(!(tick == 0 || tick == 1))
			cachemanage.operateCache(arrayResponse.get(tick-2));
		if(!(tick == 0 || tick == arrayRequest.size()+1))
			cachemanage.cacheBlockSelect();
		if(!(tick == arrayRequest.size()
				|| tick == arrayRequest.size()+1))
			cachemanage.cacheGroupSelect(arrayRequest.get(tick));
		
	}
	
	public String convertInstr(String line) {
		String instr = "";
		String ss[] = line.split(" ");
		
		
		if(ss[0].equals("0")) {
			instr = instr + "Read  ";
			instr = instr + "addr:0x" + ss[1] + "  ";
			instr = instr + "for " + ss[3] + "bits  ";
			if(ss[4].equals("0")) 
				instr = instr + "with " + "unsigned extention\n";
			else
				instr = instr + "with " + "signed extention\n";
		}
		else if(ss[0].equals("1")) {
			instr = instr + "Write  ";
			instr = instr + "data:" + ss[2] + "  ";
			instr = instr + "addr:0x" + ss[1] + "  ";
			instr = instr + "for " + ss[3] + "bits\n";
		}
		else if(ss[0].equals("2")) {
			instr = instr + "Clear all \n";
		}
		
		return instr;
	}
}