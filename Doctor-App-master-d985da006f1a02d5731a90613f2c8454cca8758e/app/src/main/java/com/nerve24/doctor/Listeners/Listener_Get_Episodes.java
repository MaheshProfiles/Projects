package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.Episode;

import java.util.ArrayList;

public interface Listener_Get_Episodes
{
    public void onGetEpisodes(ArrayList<Episode> episodeArrayList);
    public void onGetEpisodesError(String res);
}
