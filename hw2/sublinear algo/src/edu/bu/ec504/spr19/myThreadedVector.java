package edu.bu.ec504.spr19;

public class myThreadedVector<BASE extends Comparable<? super BASE>> extends ThreadedVector<BASE> {

    /**
     * SAMPLE search implementation ... your mileage will vary.
     * Please rewrite this code.
     */
    @Override
    Boolean search(BASE datum) {
        for (int ii=0; ii<getSize(); ii++) {

            try {
                link L = getLink(ii);
                if (L.datum.compareTo(datum)==0)
                    return true;
                else if(L.datum.compareTo(datum)<0){
                    while(L.next!=null) {
                        L = getLink(L.next);
                        if(L.datum.compareTo(datum)==0)
                            return true;
                        else if(L.datum.compareTo(datum)>0)
                            return false;

                    }
                    return false;
                }
            } catch (nullElementException ignored) {}
        }
        return false;
    }
}
