package pku.ca.cache;

import pku.ca.addrparser.AddrParser;
import pku.ca.memory.Memory;
import pku.ca.replacement.*;
import pku.ca.util.Request;
import pku.ca.util.Response;

public class CacheManager {
	Cache cache;
	Memory memory;
	
	Request B2Trequest;
	public int B2Taddr;
	public int B2Ttag;
	public int B2Tindex;
	public int B2Toffset;
	
	Request T2Orequest;
	public boolean isHit = true;
	public int T2Oaddr;
	public int T2Otag;
	public int T2Oindex;
	public int T2Ooffset;
	public int T2Ogroup;
	
	public CacheManager() {
		cache = new Cache();
		memory = new Memory();
	}
	
	/**
	 * 行选
	 * @param address
	 * @param entries
	 * @return
	 */
	public void cacheGroupSelect(Request request) {
		B2Trequest = request;
		AddrParser parser = new AddrParser();
		parser.setAddr(request.address);
		//第一周期完成地址解析
		B2Taddr = request.address;
		B2Ttag = parser.getTag();
		B2Tindex = parser.getIndex();
		B2Toffset = parser.getOffset();
	}
	/**
	 * Tag匹配
	 * @param address
	 * @param entries
	 * @return
	 */
	public void cacheBlockSelect() {
		isHit = true;
		if(B2Trequest.op != 2) {
			if(cache.examTag(B2Ttag, 0, B2Tindex)) {
				T2Ogroup = 0;
			}
			else if(cache.examTag(B2Ttag, 1, B2Tindex)){
				T2Ogroup = 1;
			}
			else {
				//发生了Miss	
				isHit = false;
				T2Ogroup = missHandler(B2Taddr);
			}
			
			//修改使用情况
			cache.numofNotused[B2Tindex][T2Ogroup] = true;
			cache.numofNotused[B2Tindex][1-T2Ogroup] = false;
			
			//传递流水线寄存器
			
			T2Oaddr = B2Taddr;
			T2Otag = B2Ttag;
			T2Oindex = B2Tindex;
			T2Ooffset = B2Toffset;
		}
		else
			isHit = false;
		T2Orequest = B2Trequest;
	}
	
	/**
	 * 读写操作
	 * @param request
	 * @param reponse
	 * @return
	 */
	public boolean operateCache(Response response) {
		if(T2Orequest.op == 0) {//读操作
			readHandler(T2Orequest, response);
			return true;
		}
		else if(T2Orequest.op == 1) {//写操作
			writeHandler(T2Orequest);
			return true;
		}
		else if(T2Orequest.op == 2) {//清除操作
			clearCacheAll();
			return true;
		}
		else {
			System.out.println("Request: 请求的操作不存在");
			return false;
		}
			
	}
	
	public boolean readHandler(Request request, Response response) {//按
		//按不同的粒度来
		byte []bdata = null;
		int data = 0;
		if(request.particle == 8){//粒度是8
			//组装data
			//得到byte
			bdata = new byte[1];
			
		}
		else if(request.particle == 16) {//粒度是16
			//组装data
			//得到byte
			bdata = new byte[2];
		}
		else if(request.particle == 32) {//粒度是32
			//组装data
			//得到byte
			bdata = new byte[4];
		}
		
		if(!cache.readEntryBytedata(T2Ooffset, T2Ogroup, T2Oindex, bdata))
			return false;	
		//还原data
		data = extent(bdata, request.ext);
		
		response.ResponseSet(request.op, request.address, data, //申请应该是32位来吗？？？
				request.particle, request.ext);
		return true;
	}
	
	public boolean writeHandler(Request request) {
		AddrParser parser = new AddrParser();
		parser.setAddr(request.address);
		
		//计算写入的byte数
		int len = request.particle/8;
		//写入cache
		if(cache.writeEntrybyte(T2Ogroup, parser.getIndex(), parser.getOffset(), request.data, len)) {
			//将这一项改为脏位
			cache.cachetable.get(T2Ogroup).get(parser.getIndex()).dirty = true;
			return true;
		}
		else
			return false;
	}
	
	public void clearCacheAll() {
		for(int i=0; i<Cache.sumofgroup; i++) {
			for(int j=0; j<Cache.sumofentry; j++) {//定位到一个CacheEntry
				
				if(cache.cachetable.get(i).get(j).valid
						&& cache.cachetable.get(i).get(j).dirty) {
					CacheEntry ce = cache.cachetable.get(i).get(j);
					int addr = ((ce.tag)<<12) | (j<<5);
					memory.setBytes(addr, CacheEntry.lenEntry, cache.cachetable.get(i).get(j).cachebyte);
				}	
				cache.cachetable.get(i).get(j).valid = false;
				cache.cachetable.get(i).get(j).dirty = false;
			}
		}
	}
	/**
	 * 
	 * @param address 需要调入该位置的内存块
	 * @return
	 */
	public int missHandler(int address) {
		AddrParser parser = new AddrParser();
		parser.setAddr(address);
		
		//计算起始位置
		int start = address/CacheEntry.lenEntry;
		start = start * CacheEntry.lenEntry;
		
		//确定替换组号
		int sel;
		
		if(!cache.examValid(0, parser.getIndex())) {//判断第一组中的行是不是空 
			sel = 0;
		}
		else if(!cache.examValid(0, parser.getIndex())) {//判断第二组中的行是不是空
			sel = 1;
		}
		else //启动替换策略找出替换组号
			sel = LRUselector.getOutnum(cache.numofNotused[parser.getIndex()]);
			
		//开始替换
		byte []replaceBlock = new byte[CacheEntry.lenEntry];
		
		if(memory.getBytes(start, CacheEntry.lenEntry, replaceBlock)) {
			//回写
			CacheEntry tmp = new CacheEntry(-1);
			cache.getEntry(parser.getIndex(), sel, tmp);
			if(tmp.valid && tmp.dirty) {
				int addr = tmp.tag<<12 | parser.getIndex()<<5; //+和移位的优先级问题
				memory.setBytes(addr, CacheEntry.lenEntry, tmp.cachebyte);
			}
			//替换写
			cache.cachetable.get(sel).get(parser.getIndex()).valid = true; //将该表项置为有效
			cache.writeEntry(sel, parser.getIndex(), parser.getTag(), replaceBlock); //写入这个表项
			
			//替换后，返回该组，用于后面的使用
			return sel;
		}
		return -1;
	}
	
	public int extent(byte []bdata, int ext) {
		int data = 0;
		byte []tmp = new byte[4];
		//完善byte数组
		for(int i=0; i<bdata.length; i++)
			tmp[i] = bdata[i];
		if(ext == 0) {//无符号扩展
			for(int i=bdata.length; i<4; i++)
				tmp[i] = 0;
		}
		else {//有符号扩展
			if(bdata[bdata.length-1]<0) {//负数：高位置为1
				for(int i=bdata.length; i<4; i++)
					tmp[i] = (byte)0xff;
			}
			else {//正数：高位置为0
				for(int i=bdata.length; i<4; i++)
					tmp[i] = 0;
			}
				
		}
		//开始扩展
		for (int i = 0; i < 4; i++) {
			int shift= i * 8;
			data +=(tmp[i] & 0x000000FF) << shift;//往高位
		}
		return data;
	}
	
	public void initMem() {
		for(int i=0; i<Memory.lengthofMemory; i++) {
			byte []b = {(byte)(i%255)};
			memory.setBytes(i, 1, b);
		}
	}
}