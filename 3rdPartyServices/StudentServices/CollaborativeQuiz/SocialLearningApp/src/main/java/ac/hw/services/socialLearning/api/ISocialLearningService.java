package ac.hw.services.socialLearning.api;

import java.util.List;

public interface ISocialLearningService {
	
	String getServerIPPort();
	List<String> getUserInterests();
	List<String> getCisNames();
	void postActivity(String cisName, String correct);

}
