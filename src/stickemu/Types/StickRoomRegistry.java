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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 *
 * @author Simon
 */
public class StickRoomRegistry {
       private LinkedHashMap<Integer, StickRoom> RoomList;
       private ScheduledExecutorService STES;
       public ReentrantReadWriteLock RoomLock = new ReentrantReadWriteLock(true);

       public LinkedHashMap<Integer, StickRoom> getRoomList()
       {
           return RoomList;
       }

        public StickRoomRegistry()
        {
            STES = Executors.newSingleThreadScheduledExecutor();
            RoomList = new LinkedHashMap<Integer, StickRoom>();
        }

        public Collection<StickRoom> GetAllRooms()
        {
            return RoomList.values();
        }

        public void RegisterRoom(StickRoom room)
        {
            this.RoomLock.writeLock().lock();
            try
            {
                //RoomList.Add(room.getName(), room);
                room.setStorageKey(this.RoomList.size());
                RoomList.put(this.RoomList.size(), room);
                return;
            }
            finally
            {
                this.RoomLock.writeLock().unlock();
            }
        }

        public void deRegisterRoom(StickRoom room)
        {
           this.RoomLock.writeLock().lock();
           try
            {
                for(StickRoom R : this.GetAllRooms())
                {
                    if (room != null && room.equals(R))
                    {
                        RoomList.remove(room.getStorageKey());
                        break;
                    }

                }
           } finally {
              this.RoomLock.writeLock().unlock();
           }
        }

        public void scheduleRoomTimer(Runnable r)
        {
            STES.scheduleAtFixedRate(r, 0, 1, TimeUnit.SECONDS);
        }

        public Boolean RoomExists(String Name)
        {
            //return RoomList.ContainsKey(Name);
            return (GetRoomFromName(Name) == null);
        }

        public StickRoom GetRoomFromName(String Name)
        {
            this.RoomLock.readLock().lock();
            try
            {
                for (StickRoom S : this.GetAllRooms())
                {
                    if (S.getName().equalsIgnoreCase(Name))
                        return S;
                }
            } finally {
                this.RoomLock.readLock().unlock();
            }
            return null;
           /*
            if (RoomList.ContainsKey(Name))
                return (StickRoom)RoomList[Name];
            return null;
            */
        }

        public String GetRoomPacketInfo()
        {
           // Console.WriteLine("Room packet info:");
            this.RoomLock.readLock().lock();
            try
            {
                StringBuilder SB = new StringBuilder();
                SB.append("_");
                for (StickRoom S : this.GetAllRooms())
                {
                    if (!S.getPrivacy() && (!(S.GetCR().getAllClients().size() >3)))
                    {

                        SB.append(";");
                        SB.append(S.getName());
                    }
                }
                SB.append(";");
             //   Console.WriteLine(SB.ToString());
                return SB.toString();
            } finally {
                this.RoomLock.readLock().unlock();
            }
        }
}
