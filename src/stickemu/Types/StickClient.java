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
import org.apache.mina.core.session.IoSession;
import stickemu.Main;
import stickemu.Tools.StickPacketMaker;

/**
 *
 * @author Simon
 */
public class StickClient {
    public static final String CLIENT_KEY = "CLIENT";

    private IoSession session;
    private String UID;
    private int kills;
    private int game_kills;
    private int game_deaths;
    private String name;
    private Boolean IsAtLobby;
    private Boolean IsReal;
    private String colour;
    private StickRoom Room;
    private Boolean IsMod;
    private Boolean IsMuted;
    private Boolean IsQuickplayChar;


    /**
     *
     * @param _session
     * @param new_UID
     */
    public StickClient(IoSession _session, String new_UID)
        {
            this.session = _session;
            this.UID = new_UID;
            this.IsAtLobby = true;
            this.IsReal = false;
            this.IsMod = false;
            this.IsMuted = false;
            this.IsQuickplayChar = false;
        }




        //SET
    /**
     *
     * @param _UID
     */
    public void setUID(String _UID)
        {
            this.UID = _UID;
        }

    /**
     *
     * @param _name
     */
    public void setName(String _name)
        {
            this.name = _name;
        }
    
    public void setModStatus(Boolean Mod)
        {
            this.IsMod = Mod;
        }
        /**
         *
         * @param _colour
         */
        public void setColour(String _colour)
        {
            this.colour = _colour;
        }

        /**
         *
         * @param _kills
         */
        public void setKills(int _kills)
        {
            this.kills = _kills;
        }

        /**
         *
         * @param _gamekills
         */
        public void setGameKills(int _gamekills)
        {
            this.game_kills = _gamekills;
        }

        /**
         *
         * @param _gamedeaths
         */
        public void setGameDeaths(int _gamedeaths)
        {
            this.game_deaths = _gamedeaths;
        }

        /**
         *
         * @param AtLobby
         */
        public void setLobbyStatus(Boolean AtLobby)
        {
            this.IsAtLobby = AtLobby;
        }


        public void setIsReal(Boolean Real)
        {
            this.IsReal = Real;
        }

        public void setMuteStatus(Boolean Mute)
        {
            this.IsMuted = Mute;
        }


        public void setRoom(StickRoom room)
        {
            this.Room = room;
        }

        public void setQuickplayStatus(Boolean IsQP)
        {
            this.IsQuickplayChar = IsQP;
        }
        //GET
        /**
         *
         * @return
         */
        public String getUID()
        {
            return this.UID;
        }

        /**
         *
         * @return
         */
        public String getName()
        {
            return this.name;
        }

        /**
         *
         * @return
         */
        public String getColour()
        {
            return this.colour;
        }

        /**
         *
         * @return
         */
        public int getKills()
        {
            return this.kills;
        }

        /**
         *
         * @return
         */
        public int getGameKills()
        {
            return this.game_kills;
        }

        /**
         *
         * @return
         */
        public int getGameDeaths()
        {
            return this.game_deaths;
        }

        /**
         *
         * @return
         */
        public Boolean getLobbyStatus()
        {
            return this.IsAtLobby;
        }

        /**
         *
         * @return
         */
        public IoSession getIoSession()
        {
            return this.session;
        }

        public Boolean getReal()
        {
            return this.IsReal;
        }

       public Boolean getModStatus()
        {
            return this.IsMod;
        }

        public Boolean getMuteStatus()
        {
            return this.IsMuted;
        }

        public Boolean getQuickplayStatus()
        {
            return this.IsQuickplayChar;
        }


        public StickRoom getRoom()
        {
            return this.Room;
        }
        
        public void write(StickPacket Packet)
        {
            if (Packet.getString().length() <1)
                return;
            try
            {
                 this.session.write(Packet.getString().substring(0, Packet.getString().length() -1));
            }
            catch(Exception e)
            {
                if(this.getLobbyStatus())
                {
                    Main.getLobbyServer().getClientRegistry().deregisterClient(this);
                }
                else if(this.Room != null)
                {
                    this.Room.GetCR().deregisterClient(this);
                }
            }
            
        }

        public void writePolicyFile()
        {
            if(this.session.isConnected())
                this.session.write("<cross-domain-policy><allow-access-from domain=\"" + Main.IP + "\" to-ports=\"3724,47624,1138,1139,443,110,80\" /></cross-domain-policy>");
        }

        public void writeMessage(String Message)
        {
            this.write(StickPacketMaker.getMessagePacket(Message, this.UID));
        }
        @Override
        protected void finalize() throws Throwable {
          super.finalize();
    }
}
