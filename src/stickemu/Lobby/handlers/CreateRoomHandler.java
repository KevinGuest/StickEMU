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
public class CreateRoomHandler {
        public static void HandlePacket(StickClient client, String Packet)
        {
            String mapID = Packet.substring(2, 3);
            int cycleMode = Integer.parseInt(Packet.substring(3, 4));
            Boolean isPrivate = (Packet.substring(4, 5).equalsIgnoreCase("1"));
            String RoomName = Packet.substring(5, (Packet.length() - 6)+5);

         //   System.out.println(Packet);
           // System.out.println("Room made with properties: " + mapID + cycleMode + isPrivate + RoomName);
            if(client.getName() == null)
                client.setQuickplayStatus(true);
            else
                client.setQuickplayStatus(false);

            StickRoom newRoom = new StickRoom(RoomName, mapID, cycleMode, isPrivate);

            Main.getLobbyServer().getRoomRegistry().RegisterRoom(newRoom);
            newRoom.GetCR().registerClient(client);
            client.setRoom(newRoom);
            client.setLobbyStatus(false);
            client.write(StickPacketMaker.HeresYourUID(client));

        }
}
