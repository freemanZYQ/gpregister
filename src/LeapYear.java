/**
 * Copyright(c) Beijing Kungeek Science & Technology Ltd. 
 */
package src;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * @author mmr  mmr@kungeek.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class LeapYear {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int year=0;
		if(args.length!=0)
			year=Integer.parseInt(args[0]);
		int curYear=Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())); 
		if(year<curYear){
			System.out.println(year+"年在当前年份之前。"); 
		}else{
			if((year%4==0 && year%100!=0)||(year%400==0)) 
				System.out.println(year+"年是闰年。"); 
			else
				System.out.println(year+"年不是闰年。");    
		}
	}  
}
