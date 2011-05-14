package agents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.gui.SimpleColorMap;

@SuppressWarnings("serial")
public class AgentPortrayal2D extends RectanglePortrayal2D
{
	final Stroke stroke = new BasicStroke(1.0f);

	SimpleColorMap map = new SimpleColorMap(
			new Color[] { Color.red, Color.white, Color.pink, Color.blue, Color.yellow, Color.black });

	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		Agent a = (Agent)object;
		if (a.selected)
			paint = Color.black;
		else
			paint = map.getColor(a.doubleValue());
		super.draw(object, graphics, info);

        graphics.setColor(Color.black);
        final int x = (int)(info.draw.x - info.draw.width / 2.0);
        final int y = (int)(info.draw.y - info.draw.height / 2.0);
        final int x2 = x + (int)info.draw.width - 1;
        final int y2 = y + (int)info.draw.height - 1;
        graphics.setStroke(stroke);
		if (a.showInNetwork) {
			// draw an "X" 
			// note that since we're drawing a line along the left and top, we should start drawing at x+1 and y+1
	        graphics.drawLine(x+1, y+1, x2, y2);	// draw the \
	        graphics.drawLine(x2, y+1, x+1, y2);	// draw the /
		}
		
		// draw a line down the left and top
        graphics.drawLine(x, y, x2, y);
        graphics.drawLine(x, y2, x, y);	
	}
	
	

}
