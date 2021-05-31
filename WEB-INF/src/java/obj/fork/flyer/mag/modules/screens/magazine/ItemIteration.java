package obj.fork.flyer.mag.modules.screens.magazine;

import java.util.Vector;

/*
 * iteration as forward/back navigation
 *
 */
public class ItemIteration extends Vector
{
    private int nextItem, previousItem, currentItem;

    public ItemIteration()
    {
        super();
    }

    public void setNext(int i)
    {
        nextItem = i;
    }

    public int getNext()
    {
        return nextItem;
    }

    public void setPrevious(int i)
    {
        previousItem = i;
    }

    public int getPrevious()
    {
        return previousItem;
    }

    public void setCurrent(int i)
    {
        currentItem = i;
    }

    public int getCurrent()
    {
        return currentItem;
    }

    public boolean isCurrent(int i)
    {
        return currentItem == i;
    }

    public boolean isCurrent(Object obj)
    {
        return obj.equals(
            elementAt(currentItem)
            );
    }

    public boolean isPrevious(int i)
    {
        return (currentItem -1) == i;
    }

    public boolean isNext(int i)
    {
        return (currentItem +1) == i;
    }

}
