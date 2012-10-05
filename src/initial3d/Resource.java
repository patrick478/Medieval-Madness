//package initial3d;
//
//import initial3d.engine.MeshContext;
//import initial3d.engine.MeshLOD;
//import initial3d.engine.Vec3;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//public class Resource {
//
//	public static MeshContext getWAV(String filename){
//		int maxVerPol = 0;//maximum vertices per polygon
//		
//		List<Vec3> vert = new ArrayList<Vec3>();
//		List<Vec3> norm = new ArrayList<Vec3>();
//		List<Vec3> text = new ArrayList<Vec3>();
//		List<String> poly = new ArrayList<String>();//raw format for polygons
//		
//		
//		try{
//			File wavFile = new File(filename);
//			if(!wavFile.exists()){throw new FileNotFoundException();}
//			
//			Scanner fileSc= new Scanner(wavFile);
//			
//			while(fileSc.hasNextLine()){
//				String line = fileSc.nextLine();
//				String[] arg = line.trim().split("//s+");
//				if(arg[0].equals("v")){
//					
//				}else if(arg[0].equals("vt")){
//					
//				} else if(arg[0].equals("vn")){
//					
//				} else if(arg[0].equals("vp")){
//					throw new UnsupportedOperationException("Cannot parse .obj with 'vp'");
//				} else if(arg[0].equals("f")){
//					poly.add(line);
//				}
//				
//				
//			}
//		}catch(IOException e){e.printStackTrace();
//		}catch(NumberFormatException e){e.printStackTrace();}
//		
//		
//		MeshContext meshCon = new MeshContext();
//		meshCon.
//		MeshLOD mlod = new MeshLOD(0, 0, 0, 0, 0, 0);
//	}
//	
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
