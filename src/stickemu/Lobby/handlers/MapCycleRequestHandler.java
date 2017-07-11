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
import stickemu.Main;
import stickemu.Types.StickClient;
import stickemu.Types.StickRoom;
import stickemu.Tools.StickPacketMaker;
/**
 *
 * @author Simon
 */
public class MapCycleRequestHandler {
public static void HandlePacket(StickClient client, String Packet)
        {
            String RoomName = Packet.substring(2, Packet.length() - 4);
            //Console.WriteLine(RoomName);
            StickRoom Room = Main.getLobbyServer().getRoomRegistry().GetRoomFromName(RoomName);
            if (Room != null && Room.getMapCycleList() != null)
            {
                client.write(StickPacketMaker.getMapCycleRequestResponse(Room.getMapCycleList()));
                return;
            }
            //client.write(StickPacketMaker.getMapCycleRequestResponse(Room.getMapID()));
            client.write(StickPacketMaker.getMapCycleRequestResponse("No WORKY"));
        }
}
