package si.stecce.societies.crowdtasking;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import si.stecce.societies.crowdtasking.model.Meeting;
import si.stecce.societies.crowdtasking.model.Task;

public final class Util {
	private Util() {}
	
	public static String domain = "http://crowdtasking.appspot.com";
	
	public static String formatDate(Date date) {
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		formatter.setTimeZone(TimeZone.getTimeZone("CET"));
		return formatter.format(date);
	}

	public static String taskHTMLLink(Task task) {
		return "<a href=\""+Util.taskLink(task.getId())+"\">'"+task.getTitle()+"'</a>";
	}

	public static String taskHTMLLink(Meeting meeting, Task task) {
		return "<a href=\""+Util.taskLink(task.getId())+"\">'"+meeting.getSubject()+"'</a>";
	}
	
	
	//task/view?id=X
	public static String taskLink(Long taskId) {
		if (taskId == null) {
			return "";
		}
		return domain+"/task/view?id="+taskId;
	}

	//task/view?id=X
	public static String communityLink(Long communityId) {
		if (communityId == null) {
			return "";
		}
		return domain+"/community/view?id="+communityId;
	}
/*	
	public static String getServer() {
		if (SystemProperty.environment.value() ==
			    SystemProperty.Environment.Value.Production) {
			return "http://crowdtasking.appspot.com";
			}
		return "http://localhost:8888";
		
	}
*/
	public static String readFile(String template) {
		if (template == null) {
			return null;
		}
	    CharBuffer buffer = CharBuffer.allocate(16384);
	    try {
		    FileReader reader = new FileReader(template);
		    reader.read(buffer);
		    reader.close();
	    }
	    catch (FileNotFoundException e) {
			e.printStackTrace();
	    	return null;
	    } catch (IOException e) {
			e.printStackTrace();
	    	return null;
		}
	    String index = new String(buffer.array());
	    // do template magic
	    // ...
	    return index;
	}

	public static String getTrustLevelDescription(Double trustValue) {
		if (trustValue > 0.8) {
			return "trusted";
		}
		if (trustValue > 0.3) {
			return "marginallytrusted";
		}
		if (trustValue > -0.1) {
			return "distrusted";
		}
		return "unknown";
	}
}
