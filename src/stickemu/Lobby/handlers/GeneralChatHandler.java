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
import stickemu.Tools.StickPacketMaker;
import stickemu.Tools.DatabaseTools;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import stickemu.Tools.PasswordHasher;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Simon
 */
public class GeneralChatHandler {
    public static void HandlePacket(StickClient client, String Packet)
        {

            if (client != null)
            {
                if (Packet.length() > 9 && (Packet.substring(1, 10).equalsIgnoreCase("!setcolor")))
                {
                    setNewColor(client, Packet.substring(11).replaceAll("\0", "").split(" "));
                    return;
                }

                if (Packet.length() > 12 && (Packet.substring(1, 12).equalsIgnoreCase("::modverify")))
                {
                    ModVerify(client, Packet);
                    return;
                }

                if(Packet.substring(1, 3).equalsIgnoreCase("::"))
                {
                    ModCommandHandler.ProcessModCommand(client, Packet.substring(1).replaceAll("\0", ""));
                    return;
                }

                if(client.getMuteStatus())
                {
                    client.writeMessage("SERVER MESSAGE: Unable to send chat message as you have been muted.");
                    return;
                }
                String UIDFrom = client.getUID();
                String Text = Packet.substring(1);
                if (client.getLobbyStatus())
                    Main.getLobbyServer().BroadcastPacket(StickPacketMaker.GeneralChat(UIDFrom, Text));
                else
                    client.getRoom().BroadcastToRoom(StickPacketMaker.GeneralChat(UIDFrom, Text));
            }

        }
    
    private static void ModVerify(StickClient client, String Packet)
        {
        ResultSet result = null;
        String toCheck_User = "";
        String toCheck_Pass = "";
        String[] Splitted = Packet.replaceAll("\0", "").split(" ");
        if(Splitted.length != 3)
        {
            client.writeMessage("::modverify syntax: ::modverify <username> <password>");
            return;
        }
        toCheck_User = Splitted[1];
        int rowCount = -1;
                    if(!toCheck_User.equalsIgnoreCase(client.getName()))
                    {
                        client.writeMessage("SERVER MESSAGE: Verification unsuccessful: invalid username supplied.");
                    }
                    try
                    {
                         toCheck_Pass = PasswordHasher.generateHashedPassword(Splitted[2]);
                    }
                    catch(NoSuchAlgorithmException e){}
                    try
                    {
                        synchronized (DatabaseTools.lock)
                        {
                          PreparedStatement ps = DatabaseTools.getDbConnection().prepareStatement("SELECT * FROM `users` WHERE `userNAME` = ? AND `userPASS` = ? AND `user_level` = '1'");
                          ps.setString(1, toCheck_User);
                          ps.setString(2, toCheck_Pass);
                          rowCount = DatabaseTools.getRowCount(ps);
/*                          result = ps.executeQuery();
                          result.last();
*/                      }

                    } catch(SQLException e){
                        System.out.println(e.toString());
                    }
                    if (rowCount != -1)
                    {

                            if (rowCount > 0)
                                {
                                    //is a mod
                                    client.setModStatus(true);
                                    client.writeMessage("SERVER MESSAGE: You have successfully verified as a Moderator.");
                                } else {
                                    client.writeMessage("SERVER MESSAGE: Verification unsuccessful: Either you are not a moderator, or your login details are wrong.");
                                }
                        }
                     return;
            }

    private static void setNewColor(StickClient client, String[] colour)
    {
        if(colour.length != 3)
        {
            client.writeMessage("!setcolor syntax: !color <red> <green> <blue>");
            return;
        }

        String red = colour[0];
        String green = colour[1];
        String blue = colour[2];

        int i_red = 1000;
        int i_green = 1000;
        int i_blue = 1000;

        try
        {
            i_red = Integer.parseInt(red);
            i_green = Integer.parseInt(green);
            i_blue = Integer.parseInt(blue);
        } catch (NumberFormatException e)
        {
            client.writeMessage("Error changing color: One of the supplied arguments was invalid.");
            return;
        }

        if((i_red == 0 && i_green == 0 && i_blue == 0) && !client.getModStatus())
        {
            client.writeMessage("Error changing color: Changing to the color 000 000 000 is not allowed.");
            return;
        }

        if((i_red > 255) || (i_green > 255) || (i_blue > 255))
        {
            client.writeMessage("Error changing color: Max value for arguments is 255.");
            return;
        }

        if((i_red < -99) || (i_green < -99) || (i_blue < -99))
        {
            client.writeMessage("Error changing color: Minimum value for arguments is -99.");
            return;
        }
        synchronized(DatabaseTools.lock)
        {
            try
            {
                PreparedStatement ps = DatabaseTools.getDbConnection().prepareStatement("UPDATE users SET `red` = ?, `green` = ?, `blue` = ? WHERE `username` = ?");
                ps.setString(1, red);
                ps.setString(2, green);
                ps.setString(3, blue);
                ps.setString(4, client.getName());
                if (ps.executeUpdate() == 1)
                {
                    client.writeMessage("Color successfully changed. Please log out to see the changes.");
                }
                else
                {
                    client.writeMessage("Updating color failed.");
                }
            }
            catch(SQLException e)
            {
                System.out.println("Exception changing colour of user " + client.getName() + ". Exception thrown: " + e.toString());
            }
        }
    }
                  
                

}
