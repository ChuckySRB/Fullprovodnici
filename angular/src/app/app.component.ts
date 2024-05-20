import { Component } from '@angular/core';
import { AppService } from './app.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular';

  file_name = ""
  video_id = ""
  message = ""
  loading = false
  video = null
  success = false
  fail = false

  constructor (private service: AppService){}

  onFileSelected(event) {
    this.video = event.target.files[0];
    if (this.video)
      this.file_name = this.video.name
  }

  uploadFile(){
      document.getElementById("file-input").click()
  }

  verify(){
    if (this.file_name.length == 0 || this.video_id.length == 0)
      this.message = "Enter the video and video ID first!"
    else{
      this.loading = true;
      this.service.verify(this.video, this.video_id).subscribe((result: any) => {
        if (result.match){
          this.success = true;
          this.fail = false;
          this.loading = false;
          this.message = "AUTHENTIC"
        }
        else{
          this.success = false;
          this.fail = true;
          this.loading = false;
          this.message = "DENIED"
        }
      },(error) => {
        this.loading = false; // Set loading to false in case of an error
        this.message = 'Invalid Video ID'; // Update the message with a more informative error
      })
    }
  }
}
