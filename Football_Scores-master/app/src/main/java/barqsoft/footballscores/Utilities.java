package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities
{
    public static String getLeague(Context context, int league_num) {
        final int BUNDESLIGA        = Integer.parseInt(context.getString(R.string.api_league_code_bundesliga1));
        final int LIGUE             = Integer.parseInt(context.getString(R.string.api_league_code_ligue1));
        final int PREMIER_LEAGUE    = Integer.parseInt(context.getString(R.string.api_league_code_premier_league));
        final int PRIMERA_DIVISION  = Integer.parseInt(context.getString(R.string.api_league_code_primera_division));
        final int SEGUNDA_DIVISION  = Integer.parseInt(context.getString(R.string.api_league_code_segunda_division));
        final int SERIE_A           = Integer.parseInt(context.getString(R.string.api_league_code_serie_a));
        final int PRIMERA_LIGA      = Integer.parseInt(context.getString(R.string.api_league_code_primera_liga));
        final int EREDIVISIE        = Integer.parseInt(context.getString(R.string.api_league_code_eredivisie));
        final int CHAMPIONS_LEAGUE  = Integer.parseInt(context.getString(R.string.api_league_code_champion_league));

        if (league_num == BUNDESLIGA) return context.getString(R.string.league_bundesliga);
        else if (league_num == LIGUE) return context.getString(R.string.league_ligue1);
        else if (league_num == PREMIER_LEAGUE) return context.getString(R.string.league_premier_league);
        else if (league_num == PRIMERA_DIVISION) return context.getString(R.string.league_primera_division);
        else if (league_num == SEGUNDA_DIVISION) return context.getString(R.string.league_segunda_division);
        else if (league_num == SERIE_A) return context.getString(R.string.league_serie_a);
        else if (league_num == PRIMERA_LIGA) return context.getString(R.string.league_primera_liga);
        else if (league_num == EREDIVISIE) return context.getString(R.string.league_eredivisie);
        else if (league_num == CHAMPIONS_LEAGUE) return context.getString(R.string.league_uefa_champions_league);
        else return context.getString(R.string.league_unknown);
    }
    public static String getMatchDay(Context context, int match_day,int league_num) {
        final int CHAMPIONS_LEAGUE = Integer.parseInt(context.getString(R.string.api_league_code_champion_league));

        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return context.getString(R.string.match_day_group_stages);
            }
            else if(match_day == 7 || match_day == 8)
            {
                return context.getString(R.string.match_day_first_knockout_round);
            }
            else if(match_day == 9 || match_day == 10)
            {
                return context.getString(R.string.match_day_quarter_final);
            }
            else if(match_day == 11 || match_day == 12)
            {
                return context.getString(R.string.match_day_semi_final);
            }
            else
            {
                return context.getString(R.string.match_day_final);
            }
        }
        else
        {
            return context.getString(R.string.match_day_default_text, String.valueOf(match_day));
        }
    }

    public static String getScores(int home_goals,int away_goals) {
        if(home_goals < 0 || away_goals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(away_goals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    public static int inversePositionForRTL(int position, int total) {
        //author : Udacity student josen (Jose)
        //source : https://discussions.udacity.com/t/layout-mirroring-rtl-what-is-expected/30120/16

        return total - position - 1;
    }
}
