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
package stickemu.Lobby;
import stickemu.Types.StickClient;
import stickemu.Tools.StickPacketMaker;
import stickemu.Lobby.handlers.GenericSendDataHandler;
import stickemu.Lobby.handlers.GeneralChatHandler;
import stickemu.Lobby.handlers.NewClientHandler;
import stickemu.Lobby.handlers.RoomRequestHandler;
import stickemu.Lobby.handlers.RoomDetailRequestHandler;
import stickemu.Lobby.handlers.MapCycleRequestHandler;
import stickemu.Lobby.handlers.CreateRoomHandler;
/**
 *
 * @author Simon
 */
public class PacketHandlerLobby {

public static void HandlePacket(String Packet, StickClient client)
        //public static object HandlePacket(object state)
        {
            if (Packet.length() < 2) { return; }
           // Console.WriteLine("Packet being handled from " + client.getName() + " : " + Packet);

                if (Packet.substring(0, 1).equalsIgnoreCase("0"))
                {


                            if(Packet.substring(0,2).equalsIgnoreCase("08"))
                            {
                                //Program.LS.GetLobbyThreadPool().QueueWorkItem(new Amib.Threading.WorkItemCallback(client.write), StickPacketMaker.ServerHello());
                                client.write(StickPacketMaker.ServerHello());
                            }

                            else if(Packet.substring(0,2).equalsIgnoreCase("0\0"))
                                return;

                            else if(Packet.substring(0,2).equalsIgnoreCase("01"))
                            {
                                RoomRequestHandler.handlePacket(client);
                                return;
                            }


                            else if(Packet.substring(0,2).equalsIgnoreCase("03"))
                            {
                                NewClientHandler.HandlePacket(client, Packet);
                                return;
                            }

                            else if(Packet.substring(0,2).equalsIgnoreCase("00")) //Send specified data to specified UID
                            {
                                GenericSendDataHandler.HandlePacket(client, Packet);
                                return;
                            }

                            else if(Packet.substring(0,2).equalsIgnoreCase("02"))
                            {
                                CreateRoomHandler.HandlePacket(client, Packet);
                                return;
                            }

                            else if(Packet.substring(0,2).equalsIgnoreCase("04"))
                            {
                                RoomDetailRequestHandler.HandlePacket(client, Packet);
                                return;
                            }

                            else if(Packet.substring(0,2).equalsIgnoreCase("06"))
                            {
                                MapCycleRequestHandler.HandlePacket(client, Packet);
                                return;
                            }
                }

                else if(Packet.substring(0, 1).equalsIgnoreCase("9"))
                {
                        GeneralChatHandler.HandlePacket(client, Packet);
                        return;
                }
                else
                {
                       // Console.WriteLine("Unhandled packet from " + client.getClient().Client.RemoteEndPoint + ":");
                        //System.out.printf("Unhandled packet received by LobbyPacketHandler: %s", Packet);
                        return;
                }

            }

        }
