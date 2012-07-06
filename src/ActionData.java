import java.io.Serializable;

public class ActionData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int caretPosition;
	private String panelText;
	private boolean isChanged;
	
	ActionData(int caretposition, String panelText, boolean isChanged) 
	{
		this.setCaretPosition(caretPosition);
		this.setPanelText(panelText);
		this.setChanged(isChanged);
	}
	
	public String getPanelText() 
	{
		return panelText;
	}
	public void setPanelText(String panelText) 
	{
		this.panelText = panelText;
	}
	public int getCaretPosition() 
	{
		return caretPosition;
	}
	public void setCaretPosition(int caretPosition) 
	{
		this.caretPosition = caretPosition;
	}
	public boolean getChanged() 
	{
		return isChanged;
	}
	public void setChanged(boolean isChanged) 
	{
		this.isChanged = isChanged;
	}
	
}
