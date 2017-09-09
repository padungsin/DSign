package com.openfog.condo.dsign.util;

import java.text.DecimalFormat;

public class JasperUtil {

	public static String formatCurrency(double value){
		DecimalFormat df = new DecimalFormat("#,###.00");
		return df.format(value);
		
	}

	public static String formatNumber(double value){
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(value);
		
	}
	
}
