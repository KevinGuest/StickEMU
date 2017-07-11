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
package stickemu.Tools;

import stickemu.Types.StickClient;
import stickemu.Types.StickClientRegistry;
import stickemu.Types.StickPacket;
import stickemu.Main;

import java.util.ArrayList;



/**
 *
 * @author Simon
 */
public class StickPacketMaker {
        public static StickPacket ServerHello()
        {
            StickPacket Result = new StickPacket();
            Result.Append("08\0");
            return Result;
        }

        public static StickPacket HeresYourUID(StickClient client)
        {
            StickPacket Result = new StickPacket();
            Result.Append("C");
            Result.Append(client.getUID());
            Result.Append("\0");
            return Result;
        }



        public static StickPacket getRoomList()
        {
            StickPacket Result = new StickPacket();
            Result.Append("01");
            Result.Append(Main.getLobbyServer().getRoomRegistry().GetRoomPacketInfo());
            Result.Append("\0");
            return Result;
        }

    public static StickPacket GenericSendPacket(String MyUID, String PacketData)
        {
            StickPacket Result = new StickPacket();
            Result.Append("M");
            Result.Append(MyUID);
            Result.Append(PacketData);
            return Result;
        }

        public static void EchoPacket(byte[] bytes)
        {
            for (int i = 0; i < bytes.length; i++)
            {
                System.out.printf("{0:X} ", bytes[i]);
            }
            System.out.println("\n");

        }

        public static StickPacket GeneralChat(String UID, String ChatText)
        {
            StickPacket Result = new StickPacket();
            Result.Append("M");
            Result.Append(UID);
            Result.Append("9");
            Result.Append(ChatText);
            return Result;
        }

        public static StickPacket Disconnected(String UID)
        {
            StickPacket Result = new StickPacket();
            Result.Append("D");
            Result.Append(UID);
            Result.Append("\0");
            return Result;
        }

        public static StickPacket getSendRoundDetail(String mapID, int cyclemode, int players, int RoundTime)
        {
            StickPacket Result = new StickPacket();
            Result.Append("04");
            Result.Append(mapID);
            Result.Append(Integer.toString(cyclemode));

            Result.Append(Integer.toString(players));
            Result.Append(Integer.toString(RoundTime + 31));
            Result.Append("\0");
            return Result;
        }

        public static StickPacket getMapCycleRequestResponse(String mapID)
        {
            StickPacket Result = new StickPacket();
            Result.Append("06mp=");
            Result.Append(mapID);
            Result.Append("\0");
            return Result;
        }

        public static StickPacket getSendRoundTimeTest(int RoundTime)
        {
            StickPacket Result = new StickPacket();
            Result.Append("04");
            Result.Append(Integer.toString(RoundTime));
            Result.Append("\0");
            return Result;
        }

        public static StickPacket getBroadcastPacket(String Packet, String UIDFrom)
        {
            StickPacket Result = new StickPacket();
            Result.Append("M");
            Result.Append(UIDFrom);
            Result.Append(Packet);
            return Result;
        }

        public static StickPacket getMessagePacket(String Message, String UID)
        {
            StickPacket Result = new StickPacket();
            Result.Append("M");
            Result.Append(UID);
            Result.Append("9");
            Result.Append(Message);
            Result.Append("X");
            return Result;
        }

        public static StickPacket getUserList(StickClientRegistry Clients, String UIDFrom, Boolean lobby, StickClient client)
        {
            StickPacket Result = new StickPacket();
            ArrayList<StickClient> ToDC = new ArrayList<StickClient>();
            Clients.ClientsLock.readLock().lock();
            try
            {
                for (StickClient SC : Clients.getAllClients())
                {
                    try
                    {
                            Result.Append("C"); //dummy client so we can harvest information for first connected client
                            Result.Append("***");
                            Result.Append("\0");

                        if ((lobby && SC.getLobbyStatus()) && (SC.getIoSession() != null))
                        {
                            Result.Append("C");
                            Result.Append(SC.getUID());
                            Result.Append("\0");
                          /*  Result.Append("M");
                            Result.Append(SC.getUID());
                            Result.Append("2");
                            Result.Append(StringTool.PadStringLeft(SC.getName(), "0", 20));
                            //Result.Append("\0");
                            Result.Append(SC.getColour());
                            Result.Append(Integer.toString(SC.getKills()));
                            Result.Append("\0");*/
                        }
                    }
                    catch (Exception e)
                    {
                        ToDC.add(SC);
                    }
                }
            }
            finally
            {
                Clients.ClientsLock.readLock().unlock();
            }

            for(StickClient SC : ToDC)
            {
                Clients.deregisterClient(SC);
            }
            ToDC.removeAll(ToDC);
            Result.Append("1");
            return Result;
         }
        

    public static StickPacket getUserListGame(StickClientRegistry Clients, String UIDFrom, Boolean lobby, StickClient client)
        {
            StickPacket Result = new StickPacket();
            String clientUID = client.getUID();
            ArrayList<StickClient> ToDC = new ArrayList<StickClient>();
            Clients.ClientsLock.readLock().lock();
            try
            {
                for (StickClient SC : Clients.getAllClients())
                {
                    try
                    {
                        if(!clientUID.equalsIgnoreCase(SC.getUID()))
                        {
                            Result.Append("C");
                            Result.Append(SC.getUID());
                            Result.Append("\0");
                        }
                    }
    /*
                        if (SC.getName() != null)
                        {
                            Result.Append(SC.getUID());
                            Result.Append("2");
                            Result.Append("9"); //temp wins
                            Result.Append(StringTool.PadStringLeft(Integer.toString(SC.getGameKills()), "0", 2));
                            Result.Append(StringTool.PadStringLeft(Integer.toString(SC.getGameDeaths()), "0", 2));
                            Result.Append(StringTool.PadStringLeft(SC.getName(), "0", 20));
                            Result.Append("00000"); //weapon spawns, beats me
                            Result.Append(SC.getColour());
                            Result.Append("1");
                            Result.Append("\0");
                            Result.Append(SC.getUID());
                            Result.Append("8");
                            Result.Append("0000"); // temp x
                            Result.Append("0000"); // temp y
                            Result.Append("\0");

                        }
    */

                    catch (Exception e)
                    {
                        ToDC.add(SC);
                    }

                }
            }
            finally
            {
                Clients.ClientsLock.readLock().unlock();
            }

            //clearup
            for(StickClient SC : ToDC)
            {
                Clients.deregisterClient(SC);
            }
            ToDC.removeAll(ToDC);
            
          return Result;
        }
}
