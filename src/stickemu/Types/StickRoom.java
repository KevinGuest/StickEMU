/*
 *     THIS FILE AND PROJECT IS SUPPLIED FOR EDUCATIONAL PURPOSES ONLY.
 *
 *     This program is free software; you can redistribute it
 *     and/or modify it under the terms of the GNU General
 *     Public License as published by the Free Software
 *     Foundation; either version 2 of the License, or (at your
 *     option) any later version.
 *
 *     This program is distributed in the hope that it will be
 *     useful, but WITHOUT ANY WARRANTY; without even the
 *     implied warranty of MERCHANTABILITY or FITNESS FOR A
 *     PARTICULAR PURPOSE. See the GNU General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU General
 *     Public License along with this program; if not, write to
 *     the Free Software Foundation, Inc., 59 Temple Place,
 */
package stickemu.Types;
import stickemu.Lobby.LobbyServer;
import java.util.ArrayList;
import stickemu.Main;


/**
 *
 * @author Simon
 */
public class StickRoom {
        private StickClientRegistry CR;
        private String Name;
        private String MapID;
        private int CycleMode;
        private Boolean isPrivate;
       // private Timer RoomTimer;
        private int RoundTime;
        private int StorageKey;
        private String MapCycleList;


        public StickRoom()
        {
            this.CR = new StickClientRegistry(false);
        }

        public StickRoom(String _Name, String _MapID, int CM, Boolean Priv)
        {
            this.Name = _Name;
            this.MapID = _MapID;
            this.CycleMode = CM;
            this.isPrivate = Priv;
            this.RoundTime = 300;
            //this.RoomTimer = new Timer();
            this.CR = new StickClientRegistry(false);
            //this.RoomTimer.scheduleAtFixedRate(new OnTimedEvent(), 1000, 1000);
            Main.getLobbyServer().getRoomRegistry().scheduleRoomTimer(new OnTimedEvent());

        }



        public void BroadcastToRoom(StickPacket packet)
        {
            ArrayList<StickClient> ToDC = new ArrayList<StickClient>();
            this.CR.ClientsLock.readLock().lock();
            try
            {
                for (StickClient c : this.CR.getAllClients())
                {
                    try
                    {
                        c.write(packet);
                    }
                    catch(Exception e)
                    {
                        ToDC.add(c);
                    }
                }
            } finally {
                this.CR.ClientsLock.readLock().unlock();
            }
            for (StickClient c : ToDC)
            {
                this.CR.deregisterClient(c);
            }
            ToDC.removeAll(ToDC);
        }

        public StickClientRegistry GetCR()
        {
            return CR;
        }

        public void setName(String NewName)
        {
            this.Name = NewName;
        }

        public void setMapID(String NewMapID)
        {
            this.MapID = NewMapID;
        }

        public void setCycleMode(int newCM)
        {
            this.CycleMode = newCM;
        }

        public void setPrivacy(Boolean priv)
        {
            this.isPrivate = priv;
        }

        public void setRoundTime(int time)
        {
            this.RoundTime = time;
        }
        public void setMapCycleList(String MCL)
        {
            this.MapCycleList = MCL;
        }

        public String getName()
        {
            return this.Name;
        }

        public String getMapID()
        {
            return this.MapID;
        }

        public int getCycleMode()
        {
            return this.CycleMode;
        }

        public Boolean getPrivacy()
        {
            return this.isPrivate;
        }

        public int getCurrentRoundTime()
        {
            return this.RoundTime;
        }

        public int getStorageKey()
        {
            return this.StorageKey;
        }

        public String getMapCycleList()
        {
            if (this.MapCycleList != null)
                return this.MapCycleList;
            else return null;
        }
        public void setStorageKey(int key)
        {
            this.StorageKey = key;
        }

        public void killRoom()
        {
            this.CR.ClientsLock.writeLock().lock();
            try
            {
                for (StickClient SC : this.CR.getAllClients())
                {
                    SC.getIoSession().close(false);
                }
            Main.getLobbyServer().getRoomRegistry().deRegisterRoom(Main.getLobbyServer().getRoomRegistry().GetRoomFromName(Name));
            }
            finally {
                this.CR.ClientsLock.writeLock().unlock();
            }
        }

        /*
        public RoomTimer getStickRoomTimer()
        {
            return this.Timer;
        }
        */

         class OnTimedEvent implements Runnable
         {
                public void run ()
                {
                    if(RoundTime > 0)
                  RoundTime = (RoundTime - 1);
                    else
                        RoundTime = 300;
                    if(CR.getAllClients().size() == 0)
                    {
                        Main.getLobbyServer().getRoomRegistry().deRegisterRoom(Main.getLobbyServer().getRoomRegistry().GetRoomFromName(Name));
                    }
                    try
                    {
                    this.finalize();
                    } catch (Throwable t){}
                }
         }
}

