package agents;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.LocationWrapper;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.util.Bag;

/**
 * Custom portrayal that allows us to select agents so they can draw their social network.
 * Class also allows us to draw a black rectangle around the outside of the agent grid to
 * more closely match the original implementation.
 */
@SuppressWarnings("serial")
public class AgentGridPortrayal2D extends ObjectGridPortrayal2D
{
	@Override
	public boolean setSelected(LocationWrapper wrapper, boolean selected) {
		if (wrapper == null) return true;
		if (wrapper.getFieldPortrayal() != this) return true;

		Object obj = wrapper.getObject();
		if (obj instanceof Agent) ((Agent)obj).setSelected(selected);

		return super.setSelected(wrapper, selected);
	}

	@Override
	protected void hitOrDraw(Graphics2D graphics, DrawInfo2D info, Bag putInHere) {
		super.hitOrDraw(graphics, info, putInHere);
		
		if (graphics == null)
			return;

        graphics.setColor(Color.black);
        final int x = (int)info.draw.x;
        final int y = (int)info.draw.y;
        final int x2 = x + (int)info.draw.width - 1;
        final int y2 = y + (int)info.draw.height - 1;
		
		// draw a line down the right and bottom. The agents are already drawing the left and top.
        graphics.drawLine(x2, y, x2, y2);
        graphics.drawLine(x2, y2, x, y2);
	}

}
