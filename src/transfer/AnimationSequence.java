package transfer;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Contains a set of animation items generated from the webapp's JSON
 * @author Jonathan Enderle
 *
 */
public class AnimationSequence {
	private List<AnimationItem> items;
	
	public AnimationSequence(String json) throws Exception {
		this.items = new ArrayList<>();
		
		JSONParser parser = new JSONParser();
		JSONArray items = (JSONArray) parser.parse(json);
		
		for(Object item : items) {
			JSONObject jsonItem = (JSONObject) item;
			String color = (String) jsonItem.get("color");
			int displayTime  = Integer.parseInt((String) jsonItem.get("displayTime"));
			int transitionTime = Integer.parseInt((String) jsonItem.get("transitionTime"));
			String transitionType = (String) jsonItem.get("transitionType");
			
			AnimationItem animationItem = new AnimationItem(color, displayTime, transitionTime, transitionType);
			this.items.add(animationItem);
		}
	}
	
	public List<AnimationItem> getItems() {
		return this.items;
	}

}
