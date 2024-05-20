import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) { }

  uri = "http://127.0.0.1:5000"

  verify(video: File, video_id: string){
    const options = { withCredentials: true }; 
    const data = new FormData();
    data.append("video_id" ,video_id)
    data.append("video", video, video.name)
    return this.http.post(`${this.uri}/checkRecord`, data, options)

  }
}
