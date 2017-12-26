package UI;

import java.awt.*;

/**
 * Created by aarohi on 1/10/16.
 */
public class ExpTrkScreenDetails
{
   private Dimension sSize;

    public ExpTrkScreenDetails()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        sSize = kit.getScreenSize();
    }

    public int getScreenHeight()
    {
        return sSize.height;
    }

    public int getScreenWidth()
    {
        return sSize.width;
    }

    public Dimension getCenterPosition(Dimension compDimension)
    {
        return (new Dimension(
                (sSize.width/2)- (compDimension.width/2),
                (sSize.height/2)- (compDimension.height/2)));

    }

}
