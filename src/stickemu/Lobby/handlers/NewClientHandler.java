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
package stickemu.Lobby.handlers;
import stickemu.Types.StickClient;
import stickemu.Types.StickRoom;
import stickemu.Main;
import stickemu.Tools.StickPacketMaker;
/**
 *
 * @author Simon
 */
public class NewClientHandler {
        public static void HandlePacket(StickClient client, String Packet)
        {
            if (Packet.substring(0, 3).equalsIgnoreCase("03_"))
            {
                if (!client.getReal()) //first time
                {
                    Main.getLobbyServer().getClientRegistry().registerClient(client);
                    client.setIsReal(true);
                }
                //Console.WriteLine("NewClientHandler Accessed.");
                Main.getLobbyServer().BroadcastPacket(StickPacketMaker.HeresYourUID(client));
               // Program.LS.GetLobbyThreadPoolSend().QueueWorkItem(new Amib.Threading.WorkItemCallback(client.write), StickPacketMaker.HeresYourUID(client));
                client.write(StickPacketMaker.getUserList(Main.getLobbyServer().getClientRegistry(), client.getUID(), true, client));
            }
            else
            {
                if(client.getName() == null)
                {
                    client.setQuickplayStatus(true);
                    client.setIsReal(true);
                   // client.getIoSession().close(false);
                   // return;
                }
                else
                {
                    client.setQuickplayStatus(false);
                    client.setIsReal(true);
                }
                
                String RoomName = Packet.substring(2, (Packet.length() - 3)+2);
                try
                {
                    StickRoom Room = Main.getLobbyServer().getRoomRegistry().GetRoomFromName(RoomName);
                    client.setLobbyStatus(false);
                    Room.GetCR().registerClient(client);
                    client.setRoom(Room);
                    if(!client.getQuickplayStatus())
                        Main.getLobbyServer().BroadcastPacket(StickPacketMaker.Disconnected(client.getUID()));
                    Room.BroadcastToRoom(StickPacketMaker.HeresYourUID(client));
                    client.write(StickPacketMaker.getUserListGame(Room.GetCR(), client.getUID(), false, client));
                }
                catch (Exception e)
                {
                    client.setLobbyStatus(true);
                    Main.getLobbyServer().getRoomRegistry().GetRoomFromName(RoomName).GetCR().deregisterClient(client);
                    System.out.println("Exception when parsing join room packet: " + e.toString());
                    e.printStackTrace();
                }
            }

        }
}
